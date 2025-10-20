package ar.edu.huergo.clickservice.buscadorservicios.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import ar.edu.huergo.clickservice.buscadorservicios.entity.AgendaSlot;
import ar.edu.huergo.clickservice.buscadorservicios.entity.Profesional;
import ar.edu.huergo.clickservice.buscadorservicios.entity.security.Rol;
import ar.edu.huergo.clickservice.buscadorservicios.entity.security.Usuario;

@DataJpaTest
@DisplayName("Tests de Integración - AgendaSlotRepository")
class AgendaSlotRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AgendaSlotRepository agendaSlotRepository;

    @Autowired
    private ProfesionalRepository profesionalRepository;

    private Profesional profesionalDisponible;
    private Profesional profesionalNoDisponible;
    private AgendaSlot slotDisponible;
    private AgendaSlot slotReservado;

    @BeforeEach
    void setUp() {
        Rol rolProfesional = entityManager.persistAndFlush(new Rol("PROFESIONAL"));

        profesionalDisponible = crearProfesional("maria.lopez@clickservice.com", "María López", rolProfesional);
        profesionalNoDisponible = crearProfesional("carlos.ramos@clickservice.com", "Carlos Ramos", rolProfesional);

        slotDisponible = new AgendaSlot();
        slotDisponible.setProfesional(profesionalDisponible);
        slotDisponible.setFechaInicio(LocalDateTime.of(2024, 10, 21, 9, 0));
        slotDisponible.setFechaFin(LocalDateTime.of(2024, 10, 21, 11, 0));
        slotDisponible.setDisponible(true);
        slotDisponible = entityManager.persistAndFlush(slotDisponible);

        slotReservado = new AgendaSlot();
        slotReservado.setProfesional(profesionalDisponible);
        slotReservado.setFechaInicio(LocalDateTime.of(2024, 10, 22, 14, 0));
        slotReservado.setFechaFin(LocalDateTime.of(2024, 10, 22, 16, 0));
        slotReservado.setDisponible(false);
        slotReservado = entityManager.persistAndFlush(slotReservado);

        AgendaSlot slotOtroProfesional = new AgendaSlot();
        slotOtroProfesional.setProfesional(profesionalNoDisponible);
        slotOtroProfesional.setFechaInicio(LocalDateTime.of(2024, 10, 23, 10, 0));
        slotOtroProfesional.setFechaFin(LocalDateTime.of(2024, 10, 23, 12, 0));
        slotOtroProfesional.setDisponible(true);
        entityManager.persistAndFlush(slotOtroProfesional);

        entityManager.clear();
    }

    @Test
    @DisplayName("Debería guardar y recuperar un agenda slot")
    void deberiaGuardarYRecuperarAgendaSlot() {
        Profesional profesional = profesionalRepository.findById(profesionalDisponible.getId()).orElseThrow();

        AgendaSlot nuevoSlot = new AgendaSlot();
        nuevoSlot.setProfesional(profesional);
        nuevoSlot.setFechaInicio(LocalDateTime.of(2024, 10, 24, 8, 0));
        nuevoSlot.setFechaFin(LocalDateTime.of(2024, 10, 24, 10, 0));

        AgendaSlot guardado = agendaSlotRepository.save(nuevoSlot);
        entityManager.flush();
        entityManager.clear();

        AgendaSlot recuperado = agendaSlotRepository.findById(guardado.getId()).orElseThrow();
        assertEquals(LocalDateTime.of(2024, 10, 24, 8, 0), recuperado.getFechaInicio());
        assertTrue(recuperado.getDisponible());
    }

    @Test
    @DisplayName("Debería obtener slots por profesional")
    void deberiaObtenerSlotsPorProfesional() {
        List<AgendaSlot> slots = agendaSlotRepository.findByProfesionalId(profesionalDisponible.getId());

        assertEquals(2, slots.size());
        assertTrue(slots.stream().allMatch(slot -> slot.getProfesional().getId().equals(profesionalDisponible.getId())));
    }

    @Test
    @DisplayName("Debería obtener solo slots disponibles por profesional")
    void deberiaObtenerSoloSlotsDisponiblesPorProfesional() {
        List<AgendaSlot> slotsDisponibles = agendaSlotRepository
                .findByProfesionalIdAndDisponibleTrue(profesionalDisponible.getId());

        assertEquals(1, slotsDisponibles.size());
        assertTrue(slotsDisponibles.get(0).getDisponible());
    }

    @Test
    @DisplayName("Debería detectar superposición de slots")
    void deberiaDetectarSuperposicionDeSlots() {
        boolean existeSuperposicion = agendaSlotRepository
                .existsByProfesionalIdAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
                        profesionalDisponible.getId(),
                        LocalDateTime.of(2024, 10, 21, 10, 0),
                        LocalDateTime.of(2024, 10, 21, 10, 30));

        assertTrue(existeSuperposicion);
    }

    @Test
    @DisplayName("Debería eliminar un agenda slot")
    void deberiaEliminarAgendaSlot() {
        Long slotId = slotReservado.getId();
        agendaSlotRepository.deleteById(slotId);
        entityManager.flush();

        assertFalse(agendaSlotRepository.findById(slotId).isPresent());
    }

    @Test
    @DisplayName("Debería respetar restricciones de obligatoriedad")
    void deberiaRespetarRestriccionesDeObligatoriedad() {
        AgendaSlot slotInvalido = new AgendaSlot();
        slotInvalido.setProfesional(profesionalDisponible);

        assertThrows(Exception.class, () -> {
            entityManager.persistAndFlush(slotInvalido);
        });
    }

    private Profesional crearProfesional(String email, String nombreCompleto, Rol rol) {
        Usuario usuario = new Usuario();
        usuario.setUsername(email);
        usuario.setPassword("$2a$10$password");
        usuario.setRoles(Set.of(rol));
        usuario = entityManager.persistAndFlush(usuario);

        Profesional profesional = new Profesional();
        profesional.setUsuario(usuario);
        profesional.setNombreCompleto(nombreCompleto);
        profesional.setTelefono("11-1111-1111");
        profesional.setDescripcion("Profesional certificado");
        profesional.setCalificacionPromedio(4.5);
        profesional.setTrabajosRealizados(10);
        profesional.setDisponible(true);
        profesional.setZonaTrabajo("CABA");
        profesional.setServicios(Set.of());
        return entityManager.persistAndFlush(profesional);
    }
}
