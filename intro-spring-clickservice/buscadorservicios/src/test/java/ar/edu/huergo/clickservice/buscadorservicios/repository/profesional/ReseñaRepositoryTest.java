package ar.edu.huergo.clickservice.buscadorservicios.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import ar.edu.huergo.clickservice.buscadorservicios.entity.Profesional;
import ar.edu.huergo.clickservice.buscadorservicios.entity.Reseña;
import ar.edu.huergo.clickservice.buscadorservicios.entity.security.Usuario;
import jakarta.validation.ConstraintViolationException;

@DataJpaTest
@DisplayName("Tests de Integración - ReseñaRepository")
class ReseñaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ReseñaRepository reseñaRepository;

    private Usuario usuarioCliente;
    private Usuario usuarioProfesional;
    private Profesional profesionalGuardado;
    private Reseña reseñaExistente;

    @BeforeEach
    void setUp() {
        usuarioCliente = entityManager.persistAndFlush(crearUsuario("cliente@example.com", "12345678"));
        usuarioProfesional = entityManager.persistAndFlush(crearUsuario("profesional@example.com", "87654321"));

        profesionalGuardado = crearProfesional(usuarioProfesional);
        profesionalGuardado = entityManager.persistAndFlush(profesionalGuardado);

        reseñaExistente = new Reseña();
        reseñaExistente.setOrdenId(101L);
        reseñaExistente.setRating(5);
        reseñaExistente.setComentario("Trabajo impecable y muy puntual");
        reseñaExistente.setFecha(LocalDateTime.of(2024, 1, 15, 10, 0));
        reseñaExistente.setUsuario(usuarioCliente);
        reseñaExistente.setProfesional(profesionalGuardado);
        reseñaExistente = entityManager.persistAndFlush(reseñaExistente);

        entityManager.clear();
    }

    @Test
    @DisplayName("Debería guardar y recuperar una reseña correctamente")
    void deberiaGuardarYRecuperarReseña() {
        Reseña reseña = new Reseña();
        reseña.setOrdenId(202L);
        reseña.setRating(4);
        reseña.setComentario("Muy buen profesional y cordial");
        reseña.setFecha(LocalDateTime.of(2024, 2, 20, 9, 30));
        reseña.setUsuario(usuarioCliente);
        reseña.setProfesional(profesionalGuardado);

        Reseña reseñaGuardada = reseñaRepository.save(reseña);
        entityManager.flush();
        entityManager.clear();

        Optional<Reseña> reseñaRecuperada = reseñaRepository.findById(reseñaGuardada.getId());

        assertTrue(reseñaRecuperada.isPresent());
        assertEquals(202L, reseñaRecuperada.get().getOrdenId());
        assertEquals(4, reseñaRecuperada.get().getRating());
        assertEquals("Muy buen profesional y cordial", reseñaRecuperada.get().getComentario());
        assertNotNull(reseñaRecuperada.get().getFecha());
    }

    @Test
    @DisplayName("Debería encontrar reseñas por profesional")
    void deberiaEncontrarReseñasPorProfesional() {
        Usuario otroUsuario = entityManager.persistAndFlush(crearUsuario("otro.cliente@example.com", "11223344"));

        Reseña reseñaAdicional = new Reseña();
        reseñaAdicional.setOrdenId(303L);
        reseñaAdicional.setRating(5);
        reseñaAdicional.setComentario("Servicio excelente y detallista");
        reseñaAdicional.setFecha(LocalDateTime.of(2024, 3, 5, 15, 0));
        reseñaAdicional.setUsuario(otroUsuario);
        reseñaAdicional.setProfesional(profesionalGuardado);
        entityManager.persistAndFlush(reseñaAdicional);

        Profesional otroProfesional = entityManager
                .persistAndFlush(crearProfesional(entityManager.persistAndFlush(crearUsuario("pro2@example.com", "55667788"))));

        Reseña reseñaOtroProfesional = new Reseña();
        reseñaOtroProfesional.setOrdenId(404L);
        reseñaOtroProfesional.setRating(3);
        reseñaOtroProfesional.setComentario("Trabajo correcto");
        reseñaOtroProfesional.setFecha(LocalDateTime.of(2024, 4, 1, 11, 15));
        reseñaOtroProfesional.setUsuario(otroUsuario);
        reseñaOtroProfesional.setProfesional(otroProfesional);
        entityManager.persistAndFlush(reseñaOtroProfesional);

        entityManager.clear();

        List<Reseña> reseñas = reseñaRepository.findByProfesionalId(profesionalGuardado.getId());

        assertEquals(2, reseñas.size());
        assertTrue(reseñas.stream().allMatch(r -> r.getProfesional().getId().equals(profesionalGuardado.getId())));
    }

    @Test
    @DisplayName("Debería encontrar reseñas por usuario")
    void deberiaEncontrarReseñasPorUsuario() {
        Reseña reseñaAdicional = new Reseña();
        reseñaAdicional.setOrdenId(505L);
        reseñaAdicional.setRating(4);
        reseñaAdicional.setComentario("Muy atento en todo momento");
        reseñaAdicional.setFecha(LocalDateTime.of(2024, 5, 12, 18, 45));
        reseñaAdicional.setUsuario(usuarioCliente);
        reseñaAdicional.setProfesional(profesionalGuardado);
        entityManager.persistAndFlush(reseñaAdicional);

        entityManager.clear();

        List<Reseña> reseñas = reseñaRepository.findByUsuarioId(usuarioCliente.getId());

        assertEquals(2, reseñas.size());
        assertTrue(reseñas.stream().allMatch(r -> r.getUsuario().getId().equals(usuarioCliente.getId())));
    }

    @Test
    @DisplayName("Debería encontrar reseña por orden")
    void deberiaEncontrarReseñaPorOrden() {
        Optional<Reseña> reseñaPorOrden = reseñaRepository.findByOrdenId(reseñaExistente.getOrdenId());

        assertTrue(reseñaPorOrden.isPresent());
        assertEquals(reseñaExistente.getComentario(), reseñaPorOrden.get().getComentario());
    }

    @Test
    @DisplayName("Debería verificar existencia de reseña por orden")
    void deberiaVerificarExistenciaPorOrden() {
        assertTrue(reseñaRepository.existsByOrdenId(reseñaExistente.getOrdenId()));
        assertFalse(reseñaRepository.existsByOrdenId(999L));
    }

    @Test
    @DisplayName("Debería actualizar una reseña existente")
    void deberiaActualizarReseñaExistente() {
        Reseña reseña = reseñaRepository.findById(reseñaExistente.getId()).orElseThrow();
        reseña.setRating(3);
        reseña.setComentario("Cumplió, pero podría mejorar la puntualidad");

        Reseña reseñaActualizada = reseñaRepository.save(reseña);
        entityManager.flush();
        entityManager.clear();

        Reseña reseñaVerificada = reseñaRepository.findById(reseñaExistente.getId()).orElseThrow();

        assertEquals(3, reseñaActualizada.getRating());
        assertEquals("Cumplió, pero podría mejorar la puntualidad", reseñaActualizada.getComentario());
        assertEquals(reseñaActualizada.getComentario(), reseñaVerificada.getComentario());
    }

    @Test
    @DisplayName("Debería eliminar una reseña")
    void deberiaEliminarReseña() {
        Long reseñaId = reseñaExistente.getId();
        assertTrue(reseñaRepository.existsById(reseñaId));

        reseñaRepository.deleteById(reseñaId);
        entityManager.flush();

        assertFalse(reseñaRepository.existsById(reseñaId));
    }

    @Test
    @DisplayName("Debería lanzar excepción al guardar reseña inválida")
    void deberiaLanzarExcepcionAlGuardarReseñaInvalida() {
        Reseña reseñaInvalida = new Reseña();
        reseñaInvalida.setOrdenId(null);
        reseñaInvalida.setRating(6);
        reseñaInvalida.setComentario("Corto");
        reseñaInvalida.setFecha(null);
        reseñaInvalida.setUsuario(null);
        reseñaInvalida.setProfesional(null);

        assertThrows(ConstraintViolationException.class, () -> {
            reseñaRepository.save(reseñaInvalida);
            entityManager.flush();
        });
    }

    private Usuario crearUsuario(String email, String dni) {
        Usuario usuario = new Usuario();
        usuario.setNombre("Nombre");
        usuario.setApellido("Apellido");
        usuario.setDni(dni);
        usuario.setTelefono("+54 11 5555-5555");
        usuario.setCalle("Calle Ejemplo");
        usuario.setAltura(123);
        usuario.setUsername(email);
        usuario.setPassword("contraseñaSegura123");
        return usuario;
    }

    private Profesional crearProfesional(Usuario usuario) {
        Profesional profesional = new Profesional();
        profesional.setUsuario(usuario);
        profesional.setNombreCompleto("Profesional Demo");
        profesional.setTelefono("+54 11 4444-4444");
        profesional.setDescripcion("Especialista en mantenimiento general");
        profesional.setCalificacionPromedio(4.2);
        profesional.setTrabajosRealizados(12);
        profesional.setDisponible(true);
        profesional.setZonaTrabajo("CABA");
        return profesional;
    }
}
