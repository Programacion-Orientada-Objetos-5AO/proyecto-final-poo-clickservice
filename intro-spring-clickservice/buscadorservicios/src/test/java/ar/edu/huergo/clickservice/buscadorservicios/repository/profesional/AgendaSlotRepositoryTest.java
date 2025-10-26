package ar.edu.huergo.clickservice.buscadorservicios.repository.profesional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import ar.edu.huergo.clickservice.buscadorservicios.entity.profesional.Profesional;
import ar.edu.huergo.clickservice.buscadorservicios.entity.security.Usuario;
import ar.edu.huergo.clickservice.buscadorservicios.entity.servicio.Servicio;

@DataJpaTest
class ProfesionalRepositoryTest {

    @Autowired
    private ProfesionalRepository profesionalRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Servicio servicioPlomeria;
    private Servicio servicioElectricidad;
    private Profesional profesionalDisponible;
    private Profesional profesionalNoDisponible;

    @BeforeEach
    void setUp() {
        servicioPlomeria = new Servicio(null, "Plomería", 30.0);
        servicioElectricidad = new Servicio(null, "Electricidad", 45.0);
        entityManager.persist(servicioPlomeria);
        entityManager.persist(servicioElectricidad);

        Usuario usuarioDisponible = crearUsuario(1L, "disponible@example.com", "20123456");
        Usuario usuarioNoDisponible = crearUsuario(2L, "nodisponible@example.com", "30123456");
        entityManager.persist(usuarioDisponible);
        entityManager.persist(usuarioNoDisponible);

        profesionalDisponible = crearProfesional(usuarioDisponible, true);
        profesionalDisponible.setServicios(Set.of(servicioPlomeria));
        profesionalNoDisponible = crearProfesional(usuarioNoDisponible, false);
        profesionalNoDisponible.setServicios(Set.of(servicioElectricidad));

        entityManager.persist(profesionalDisponible);
        entityManager.persist(profesionalNoDisponible);
        entityManager.flush();
    }

    @Test
    @DisplayName("Debería encontrar profesional por usuario")
    void deberiaEncontrarProfesionalPorUsuario() {
        // When
        Optional<Profesional> profesional = profesionalRepository
                .findByUsuarioId(profesionalDisponible.getUsuario().getId());

        // Then
        assertTrue(profesional.isPresent());
        assertEquals(profesionalDisponible.getId(), profesional.get().getId());
    }

    @Test
    @DisplayName("Debería listar profesionales disponibles")
    void deberiaListarProfesionalesDisponibles() {
        // When
        List<Profesional> disponibles = profesionalRepository.findByDisponibleTrue();

        // Then
        assertEquals(1, disponibles.size());
        assertEquals(profesionalDisponible.getId(), disponibles.get(0).getId());
    }

    @Test
    @DisplayName("Debería buscar profesionales disponibles por servicio")
    void deberiaBuscarProfesionalesDisponiblesPorServicio() {
        // When
        List<Profesional> profesionales = profesionalRepository
                .findByServiciosIdAndDisponibleTrue(servicioPlomeria.getId());

        // Then
        assertEquals(1, profesionales.size());
        assertEquals(profesionalDisponible.getId(), profesionales.get(0).getId());
    }

    private Profesional crearProfesional(Usuario usuario, boolean disponible) {
        Profesional profesional = new Profesional();
        profesional.setUsuario(usuario);
        profesional.setNombreCompleto("Profesional " + usuario.getNombre());
        profesional.setTelefono("+54 9 11 4444-" + usuario.getId() + usuario.getId());
        profesional.setDescripcion("Profesional altamente capacitado");
        profesional.setDisponible(disponible);
        profesional.setZonaTrabajo("Zona Centro");
        return profesional;
    }

    private Usuario crearUsuario(Long id, String username, String dni) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNombre("Nombre" + id);
        usuario.setApellido("Apellido" + id);
        usuario.setDni(dni);
        usuario.setTelefono("+54 9 11 5555-000" + id);
        usuario.setCalle("Calle " + id);
        usuario.setAltura(100 + id.intValue());
        usuario.setUsername(username);
        usuario.setPassword("contraseña_segura_para_usuario" + id);
        return usuario;
    }
}