package ar.edu.huergo.clickservice.buscadorservicios.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ar.edu.huergo.clickservice.buscadorservicios.entity.AgendaSlot;
import ar.edu.huergo.clickservice.buscadorservicios.entity.Profesional;
import ar.edu.huergo.clickservice.buscadorservicios.repository.AgendaSlotRepository;
import ar.edu.huergo.clickservice.buscadorservicios.repository.ProfesionalRepository;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de Unidad - AgendaSlotService")
class AgendaSlotServiceTest {

    @Mock
    private AgendaSlotRepository agendaSlotRepository;

    @Mock
    private ProfesionalRepository profesionalRepository;

    @InjectMocks
    private AgendaSlotService agendaSlotService;

    private Profesional profesional;
    private AgendaSlot agendaSlot;
    private LocalDateTime inicio;
    private LocalDateTime fin;

    @BeforeEach
    void setUp() {
        profesional = new Profesional();
        profesional.setId(1L);
        profesional.setNombreCompleto("Juan Pérez");

        inicio = LocalDateTime.of(2024, 10, 20, 9, 0);
        fin = inicio.plusHours(2);

        agendaSlot = new AgendaSlot();
        agendaSlot.setId(1L);
        agendaSlot.setProfesional(profesional);
        agendaSlot.setFechaInicio(inicio);
        agendaSlot.setFechaFin(fin);
        agendaSlot.setDisponible(true);
    }

    @Test
    @DisplayName("Debería obtener todos los agenda slots")
    void deberiaObtenerTodosLosAgendaSlots() {
        when(agendaSlotRepository.findAll()).thenReturn(Arrays.asList(agendaSlot));

        List<AgendaSlot> resultado = agendaSlotService.obtenerTodosLosAgendaSlots();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(agendaSlotRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería obtener agenda slot por id cuando existe")
    void deberiaObtenerAgendaSlotPorIdCuandoExiste() {
        when(agendaSlotRepository.findById(1L)).thenReturn(Optional.of(agendaSlot));

        AgendaSlot resultado = agendaSlotService.obtenerAgendaSlotPorId(1L);

        assertNotNull(resultado);
        assertEquals(agendaSlot.getId(), resultado.getId());
        verify(agendaSlotRepository).findById(1L);
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando agenda slot no existe")
    void deberiaLanzarExcepcionCuandoAgendaSlotNoExiste() {
        when(agendaSlotRepository.findById(99L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> agendaSlotService.obtenerAgendaSlotPorId(99L));

        assertEquals("AgendaSlot no encontrado", exception.getMessage());
        verify(agendaSlotRepository).findById(99L);
    }

    @Test
    @DisplayName("Debería obtener agenda slots por profesional")
    void deberiaObtenerAgendaSlotsPorProfesional() {
        when(agendaSlotRepository.findByProfesionalId(1L)).thenReturn(List.of(agendaSlot));

        List<AgendaSlot> resultado = agendaSlotService.obtenerAgendaSlotsPorProfesional(1L);

        assertEquals(1, resultado.size());
        verify(agendaSlotRepository).findByProfesionalId(1L);
    }

    @Test
    @DisplayName("Debería crear un agenda slot correctamente")
    void deberiaCrearAgendaSlotCorrectamente() {
        AgendaSlot nuevoAgendaSlot = new AgendaSlot();
        nuevoAgendaSlot.setProfesional(profesional);
        nuevoAgendaSlot.setFechaInicio(inicio);
        nuevoAgendaSlot.setFechaFin(fin);

        when(profesionalRepository.findById(1L)).thenReturn(Optional.of(profesional));
        when(agendaSlotRepository.save(any(AgendaSlot.class))).thenAnswer(invocation -> {
            AgendaSlot slotGuardado = invocation.getArgument(0);
            slotGuardado.setId(2L);
            return slotGuardado;
        });

        AgendaSlot resultado = agendaSlotService.crearAgendaSlot(nuevoAgendaSlot);

        assertNotNull(resultado.getId());
        assertEquals(2L, resultado.getId());
        assertEquals(profesional, resultado.getProfesional());
        verify(profesionalRepository).findById(1L);
        verify(agendaSlotRepository).save(any(AgendaSlot.class));
    }

    @Test
    @DisplayName("Debería lanzar excepción al crear agenda slot sin profesional válido")
    void deberiaLanzarExcepcionAlCrearAgendaSlotSinProfesionalValido() {
        AgendaSlot nuevoAgendaSlot = new AgendaSlot();
        nuevoAgendaSlot.setFechaInicio(inicio);
        nuevoAgendaSlot.setFechaFin(fin);

        assertThrows(EntityNotFoundException.class, () -> agendaSlotService.crearAgendaSlot(nuevoAgendaSlot));
    }

    @Test
    @DisplayName("Debería actualizar agenda slot existente")
    void deberiaActualizarAgendaSlotExistente() {
        AgendaSlot datosActualizados = new AgendaSlot();
        datosActualizados.setProfesional(profesional);
        datosActualizados.setFechaInicio(inicio.plusDays(1));
        datosActualizados.setFechaFin(fin.plusDays(1));
        datosActualizados.setDisponible(false);

        when(agendaSlotRepository.findById(1L)).thenReturn(Optional.of(agendaSlot));
        when(profesionalRepository.findById(1L)).thenReturn(Optional.of(profesional));
        when(agendaSlotRepository.save(any(AgendaSlot.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AgendaSlot resultado = agendaSlotService.actualizarAgendaSlot(1L, datosActualizados);

        assertEquals(datosActualizados.getFechaInicio(), resultado.getFechaInicio());
        assertEquals(datosActualizados.getFechaFin(), resultado.getFechaFin());
        assertFalse(resultado.getDisponible());
        verify(agendaSlotRepository).findById(1L);
        verify(agendaSlotRepository).save(agendaSlot);
    }

    @Test
    @DisplayName("Debería lanzar excepción al actualizar agenda slot inexistente")
    void deberiaLanzarExcepcionAlActualizarAgendaSlotInexistente() {
        AgendaSlot datosActualizados = new AgendaSlot();
        datosActualizados.setProfesional(profesional);
        datosActualizados.setFechaInicio(inicio);
        datosActualizados.setFechaFin(fin);

        when(agendaSlotRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> agendaSlotService.actualizarAgendaSlot(99L, datosActualizados));
    }

    @Test
    @DisplayName("Debería lanzar excepción al actualizar con profesional inexistente")
    void deberiaLanzarExcepcionAlActualizarConProfesionalInexistente() {
        Profesional otroProfesional = new Profesional();
        otroProfesional.setId(5L);

        AgendaSlot datosActualizados = new AgendaSlot();
        datosActualizados.setProfesional(otroProfesional);
        datosActualizados.setFechaInicio(inicio);
        datosActualizados.setFechaFin(fin);

        when(agendaSlotRepository.findById(1L)).thenReturn(Optional.of(agendaSlot));
        when(profesionalRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> agendaSlotService.actualizarAgendaSlot(1L, datosActualizados));
    }

    @Test
    @DisplayName("Debería eliminar agenda slot correctamente")
    void deberiaEliminarAgendaSlotCorrectamente() {
        when(agendaSlotRepository.findById(1L)).thenReturn(Optional.of(agendaSlot));

        assertDoesNotThrow(() -> agendaSlotService.eliminarAgendaSlot(1L));

        verify(agendaSlotRepository).delete(agendaSlot);
    }

    @Test
    @DisplayName("Debería lanzar excepción al eliminar agenda slot inexistente")
    void deberiaLanzarExcepcionAlEliminarAgendaSlotInexistente() {
        when(agendaSlotRepository.findById(42L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> agendaSlotService.eliminarAgendaSlot(42L));
    }
}
