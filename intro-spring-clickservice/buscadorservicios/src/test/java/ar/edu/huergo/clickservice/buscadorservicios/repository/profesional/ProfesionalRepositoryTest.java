package ar.edu.huergo.clickservice.buscadorservicios.repository.profesional;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

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

    @Test
    void deberiaEncontrarProfesionalPorUsuarioId() {
        Servicio servicio = persistirServicio(1L, "Electricidad", 1200.0);
        Profesional profesional = persistirProfesional(1L, true, servicio);

        entityManager.clear();

        var resultado = profesionalRepository.findByUsuarioId(profesional.getUsuario().getId());

        assertThat(resultado)
            .as("Debe encontrarse el profesional asociado al usuario")
            .isPresent()
            .get()
            .extracting(Profesional::getId)
            .isEqualTo(profesional.getId());

        assertThat(profesionalRepository.findByUsuarioId(999L))
            .as("No debe encontrarse un profesional para un usuario inexistente")
            .isEmpty();
    }

    @Test
    void deberiaListarSoloProfesionalesDisponibles() {
        Servicio servicio = persistirServicio(2L, "Plomeria", 1500.0);
        Profesional disponible = persistirProfesional(2L, true, servicio);
        persistirProfesional(3L, false, servicio);

        entityManager.clear();

        List<Profesional> profesionales = profesionalRepository.findByDisponibleTrue();

        assertThat(profesionales)
            .as("Solo debe devolver profesionales con disponibilidad activa")
            .containsExactly(disponible)
            .allMatch(Profesional::getDisponible);
    }

    @Test
    void deberiaEncontrarDisponiblesPorServicio() {
        Servicio electricidad = persistirServicio(4L, "Electricista", 1800.0);
        Servicio gasista = persistirServicio(5L, "Gasista", 2000.0);

        Profesional profesionalDisponible = persistirProfesional(4L, true, electricidad);
        persistirProfesional(5L, true, gasista);
        persistirProfesional(6L, false, electricidad);

        entityManager.clear();

        List<Profesional> encontrados = profesionalRepository
            .findByServiciosIdAndDisponibleTrue(electricidad.getId());

        assertThat(encontrados)
            .as("Debe listar únicamente profesionales disponibles asociados al servicio indicado")
            .containsExactly(profesionalDisponible)
            .allMatch(Profesional::getDisponible);
    }

    private Profesional persistirProfesional(Long indice, boolean disponible, Servicio servicio) {
        Usuario usuario = persistirUsuario(indice);

        Profesional profesional = new Profesional();
        profesional.setUsuario(usuario);
        profesional.setNombreCompleto("Profesional " + indice);
        profesional.setTelefono("+541100000" + indice);
        profesional.setDescripcion("Descripcion " + indice);
        profesional.setDisponible(disponible);
        profesional.setZonaTrabajo("Zona " + indice);
        profesional.setServicios(new HashSet<>(Collections.singleton(servicio)));

        entityManager.persist(profesional);
        entityManager.flush();
        return profesional;
    }

    private Usuario persistirUsuario(Long indice) {
        Usuario usuario = new Usuario();
        usuario.setNombre("Nombre" + indice);
        usuario.setApellido("Apellido" + indice);
        usuario.setDni(String.format("%08d", 10000000 + indice));
        usuario.setTelefono("+54119999" + String.format("%02d", indice));
        usuario.setCalle("Calle " + indice);
        usuario.setAltura(100 + indice.intValue());
        usuario.setUsername("usuario" + indice + "@mail.com");
        usuario.setPassword("ContrasenaSegura" + indice + "XYZ");

        entityManager.persist(usuario);
        entityManager.flush();
        return usuario;
    }

    private Servicio persistirServicio(Long indice, String nombre, double precioHora) {
        Servicio servicio = new Servicio();
        servicio.setNombre(nombre + " " + indice);
        servicio.setPrecioHora(precioHora);

        entityManager.persist(servicio);
        entityManager.flush();
        return servicio;
    }
}