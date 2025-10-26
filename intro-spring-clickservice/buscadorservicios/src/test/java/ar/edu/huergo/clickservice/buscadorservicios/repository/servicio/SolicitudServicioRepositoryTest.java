package ar.edu.huergo.clickservice.buscadorservicios.repository.servicio;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import ar.edu.huergo.clickservice.buscadorservicios.entity.security.Usuario;
import ar.edu.huergo.clickservice.buscadorservicios.entity.servicio.Servicio;
import ar.edu.huergo.clickservice.buscadorservicios.entity.servicio.SolicitudServicio;
import ar.edu.huergo.clickservice.buscadorservicios.entity.servicio.SolicitudServicio.EstadoSolicitud;

@DataJpaTest
class SolicitudServicioRepositoryTest {

    @Autowired
    private SolicitudServicioRepository solicitudServicioRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Servicio servicioPlomeria;
    private Servicio servicioElectricidad;
    private Usuario clienteJuan;
    private Usuario clienteAna;

    private SolicitudServicio solicitudPendiente;
    private SolicitudServicio solicitudAsignada;
    private SolicitudServicio solicitudCompletada;

    @BeforeEach
    void setUp() {
        servicioPlomeria = new Servicio(null, "Plomería", 25.0);
        servicioElectricidad = new Servicio(null, "Electricidad", 35.0);
        entityManager.persist(servicioPlomeria);
        entityManager.persist(servicioElectricidad);

        clienteJuan = crearUsuario(1L, "juan@example.com", "20123456");
        clienteAna = crearUsuario(2L, "ana@example.com", "30123456");
        entityManager.persist(clienteJuan);
        entityManager.persist(clienteAna);

        solicitudPendiente = construirSolicitud(servicioPlomeria, clienteJuan,
                "Perdida de agua en la cocina", EstadoSolicitud.PENDIENTE, LocalDate.now().plusDays(3));
        solicitudAsignada = construirSolicitud(servicioPlomeria, clienteJuan,
                "Instalación de termotanque", EstadoSolicitud.ASIGNADA, LocalDate.now().plusDays(5));
        solicitudCompletada = construirSolicitud(servicioElectricidad, clienteAna,
                "Revisión de instalación eléctrica", EstadoSolicitud.COMPLETADA, LocalDate.now().plusDays(7));

        entityManager.persist(solicitudPendiente);
        entityManager.persist(solicitudAsignada);
        entityManager.persist(solicitudCompletada);
        entityManager.flush();
    }

    @Test
    @DisplayName("Debería encontrar solicitudes por cliente")
    void deberiaEncontrarSolicitudesPorCliente() {
        // When
        List<SolicitudServicio> solicitudes =
                solicitudServicioRepository.findByClienteId(clienteJuan.getId());

        // Then
        assertEquals(2, solicitudes.size());
        assertTrue(solicitudes.stream().allMatch(s -> s.getCliente().getId().equals(clienteJuan.getId())));
    }

    @Test
    @DisplayName("Debería filtrar solicitudes por estado")
    void deberiaFiltrarSolicitudesPorEstado() {
        // When
        List<SolicitudServicio> solicitudesAsignadas =
                solicitudServicioRepository.findByEstado(EstadoSolicitud.ASIGNADA);

        // Then
        assertEquals(1, solicitudesAsignadas.size());
        assertEquals(EstadoSolicitud.ASIGNADA, solicitudesAsignadas.get(0).getEstado());
    }

    @Test
    @DisplayName("Debería buscar solicitudes por servicio y estado")
    void deberiaBuscarSolicitudesPorServicioYEstado() {
        // When
        List<SolicitudServicio> solicitudes = solicitudServicioRepository
                .findByServicioIdAndEstado(servicioPlomeria.getId(), EstadoSolicitud.PENDIENTE);

        // Then
        assertEquals(1, solicitudes.size());
        assertEquals(EstadoSolicitud.PENDIENTE, solicitudes.get(0).getEstado());
        assertEquals(servicioPlomeria.getId(), solicitudes.get(0).getServicio().getId());
    }

    private SolicitudServicio construirSolicitud(Servicio servicio, Usuario cliente, String descripcion,
            EstadoSolicitud estado, LocalDate fechaSolicitada) {
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicio);
        solicitud.setCliente(cliente);
        solicitud.setDescripcionProblema(descripcion);
        solicitud.setDireccionServicio("Av. Siempre Viva 742");
        solicitud.setFechaSolicitada(fechaSolicitada);
        solicitud.setFranjaHoraria("Mañana");
        solicitud.setPresupuestoMaximo(20000.0);
        solicitud.setEstado(estado);
        solicitud.setFechaCreacion(LocalDateTime.now());
        solicitud.setComentariosAdicionales("Contactar antes de llegar");
        return solicitud;
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
