package ar.edu.huergo.clickservice.buscadorservicios.repository.profesional;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

import ar.edu.huergo.clickservice.buscadorservicios.entity.profesional.Profesional;
import ar.edu.huergo.clickservice.buscadorservicios.entity.profesional.Reseña;
import ar.edu.huergo.clickservice.buscadorservicios.entity.security.Usuario;

@DataJpaTest
class ReseñaRepositoryTest {

    @Autowired
    private ReseñaRepository reseñaRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Profesional profesional;
    private Usuario usuarioCliente;
    private Reseña reseña1;
    private Reseña reseña2;

    @BeforeEach
    void setUp() {
        Usuario usuarioProfesional = crearUsuario(1L, "profesional@example.com", "20123456");
        usuarioCliente = crearUsuario(2L, "cliente@example.com", "30123456");
        entityManager.persist(usuarioProfesional);
        entityManager.persist(usuarioCliente);

        profesional = crearProfesional(usuarioProfesional);
        entityManager.persist(profesional);

        reseña1 = crearReseña(101L, 5, profesional, usuarioCliente);
        reseña2 = crearReseña(102L, 4, profesional, usuarioCliente);

        entityManager.persist(reseña1);
        entityManager.persist(reseña2);
        entityManager.flush();
    }

    @Test
    @DisplayName("Debería obtener reseñas por profesional")
    void deberiaObtenerReseñasPorProfesional() {
        // When
        List<Reseña> reseñas = reseñaRepository.findByProfesionalId(profesional.getId());

        // Then
        assertEquals(2, reseñas.size());
        assertTrue(reseñas.stream().allMatch(r -> r.getProfesional().getId().equals(profesional.getId())));
    }

    @Test
    @DisplayName("Debería obtener reseñas por usuario")
    void deberiaObtenerReseñasPorUsuario() {
        // When
        List<Reseña> reseñas = reseñaRepository.findByUsuarioId(usuarioCliente.getId());

        // Then
        assertEquals(2, reseñas.size());
        assertTrue(reseñas.stream().allMatch(r -> r.getUsuario().getId().equals(usuarioCliente.getId())));
    }

    @Test
    @DisplayName("Debería encontrar reseña por orden")
    void deberiaEncontrarReseñaPorOrden() {
        // When
        Optional<Reseña> reseña = reseñaRepository.findByOrdenId(reseña1.getOrdenId());

        // Then
        assertTrue(reseña.isPresent());
        assertEquals(reseña1.getId(), reseña.get().getId());
    }

    @Test
    @DisplayName("Debería verificar existencia de reseña por orden")
    void deberiaVerificarExistenciaDeReseñaPorOrden() {
        // When & Then
        assertTrue(reseñaRepository.existsByOrdenId(reseña2.getOrdenId()));
    }

    private Reseña crearReseña(Long ordenId, int rating, Profesional profesional, Usuario usuario) {
        Reseña reseña = new Reseña();
        reseña.setOrdenId(ordenId);
        reseña.setRating(rating);
        reseña.setComentario("Comentario detallado sobre el servicio recibido");
        reseña.setFecha(LocalDateTime.now());
        reseña.setProfesional(profesional);
        reseña.setUsuario(usuario);
        return reseña;
    }

    private Profesional crearProfesional(Usuario usuario) {
        Profesional profesional = new Profesional();
        profesional.setUsuario(usuario);
        profesional.setNombreCompleto("Profesional Ejemplo");
        profesional.setTelefono("+54 9 11 5555-2222");
        profesional.setDescripcion("Servicio integral");
        profesional.setDisponible(Boolean.TRUE);
        profesional.setZonaTrabajo("Zona Oeste");
        return profesional;
    }

    private Usuario crearUsuario(Long id, String username, String dni) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNombre("Nombre" + id);
        usuario.setApellido("Apellido" + id);
        usuario.setDni(dni);
        usuario.setTelefono("+54 9 11 4444-000" + id);
        usuario.setCalle("Calle " + id);
        usuario.setAltura(200 + id.intValue());
        usuario.setUsername(username);
        usuario.setPassword("contraseña_segura_para_usuario" + id);
        return usuario;
    }
}