package ar.edu.huergo.clickservice.buscadorservicios.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
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

import ar.edu.huergo.clickservice.buscadorservicios.entity.Comision;
import ar.edu.huergo.clickservice.buscadorservicios.repository.ComisionRepository;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de Unidad - ComisionService")
class ComisionServiceTest {

    @Mock
    private ComisionRepository comisionRepository;

    @InjectMocks
    private ComisionService comisionService;

    private Comision comisionEjemplo;

    @BeforeEach
    void setUp() {
        comisionEjemplo = new Comision();
        comisionEjemplo.setId(1L);
        comisionEjemplo.setPagoId(100L);
        comisionEjemplo.setTasa(12.0);
        comisionEjemplo.setBase(1000.0);
        comisionEjemplo.setMonto(120.0);
        comisionEjemplo.setFecha(LocalDate.now());
    }

    @Test
    @DisplayName("Debería obtener todas las comisiones correctamente")
    void deberiaObtenerTodasLasComisiones() {
        List<Comision> comisionesEsperadas = Arrays.asList(comisionEjemplo);
        when(comisionRepository.findAll()).thenReturn(comisionesEsperadas);

        List<Comision> resultado = comisionService.obtenerTodasLasComisiones();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(comisionEjemplo.getPagoId(), resultado.get(0).getPagoId());
        verify(comisionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería obtener una comisión por ID cuando existe")
    void deberiaObtenerComisionPorIdCuandoExiste() {
        Long comisionId = 1L;
        when(comisionRepository.findById(comisionId)).thenReturn(Optional.of(comisionEjemplo));

        Comision resultado = comisionService.obtenerComisionPorId(comisionId);

        assertNotNull(resultado);
        assertEquals(comisionEjemplo.getId(), resultado.getId());
        verify(comisionRepository, times(1)).findById(comisionId);
    }

    @Test
    @DisplayName("Debería lanzar EntityNotFoundException cuando la comisión no existe")
    void deberiaLanzarExcepcionCuandoComisionNoExiste() {
        Long comisionIdInexistente = 999L;
        when(comisionRepository.findById(comisionIdInexistente)).thenReturn(Optional.empty());

        EntityNotFoundException excepcion = assertThrows(
                EntityNotFoundException.class,
                () -> comisionService.obtenerComisionPorId(comisionIdInexistente));

        assertEquals("Comisión no encontrada", excepcion.getMessage());
        verify(comisionRepository, times(1)).findById(comisionIdInexistente);
    }

    @Test
    @DisplayName("Debería crear una comisión correctamente")
    void deberiaCrearComisionCorrectamente() {
        Comision nuevaComision = new Comision();
        nuevaComision.setPagoId(200L);
        nuevaComision.setTasa(10.0);
        nuevaComision.setBase(800.0);
        nuevaComision.setMonto(80.0);
        nuevaComision.setFecha(LocalDate.now());

        when(comisionRepository.save(nuevaComision)).thenReturn(nuevaComision);

        Comision resultado = comisionService.crearComision(nuevaComision);

        assertNotNull(resultado);
        assertEquals(nuevaComision.getPagoId(), resultado.getPagoId());
        verify(comisionRepository, times(1)).save(nuevaComision);
    }

    @Test
    @DisplayName("Debería actualizar una comisión existente correctamente")
    void deberiaActualizarComisionExistente() {
        Long comisionId = 1L;
        Comision comisionActualizada = new Comision();
        comisionActualizada.setPagoId(300L);
        comisionActualizada.setTasa(15.0);
        comisionActualizada.setBase(1200.0);
        comisionActualizada.setMonto(180.0);
        comisionActualizada.setFecha(LocalDate.now());

        when(comisionRepository.findById(comisionId)).thenReturn(Optional.of(comisionEjemplo));
        when(comisionRepository.save(any(Comision.class))).thenReturn(comisionEjemplo);

        Comision resultado = comisionService.actualizarComision(comisionId, comisionActualizada);

        assertNotNull(resultado);
        verify(comisionRepository, times(1)).findById(comisionId);
        verify(comisionRepository, times(1)).save(comisionEjemplo);
        assertEquals(comisionActualizada.getPagoId(), comisionEjemplo.getPagoId());
        assertEquals(comisionActualizada.getTasa(), comisionEjemplo.getTasa());
        assertEquals(comisionActualizada.getBase(), comisionEjemplo.getBase());
        assertEquals(comisionActualizada.getMonto(), comisionEjemplo.getMonto());
        assertEquals(comisionActualizada.getFecha(), comisionEjemplo.getFecha());
    }

    @Test
    @DisplayName("Debería lanzar EntityNotFoundException al actualizar comisión inexistente")
    void deberiaLanzarExcepcionAlActualizarComisionInexistente() {
        Long comisionIdInexistente = 999L;
        Comision comisionActualizada = new Comision();
        comisionActualizada.setPagoId(200L);
        comisionActualizada.setTasa(10.0);
        comisionActualizada.setBase(800.0);
        comisionActualizada.setMonto(80.0);
        comisionActualizada.setFecha(LocalDate.now());

        when(comisionRepository.findById(comisionIdInexistente)).thenReturn(Optional.empty());

        EntityNotFoundException excepcion = assertThrows(
                EntityNotFoundException.class,
                () -> comisionService.actualizarComision(comisionIdInexistente, comisionActualizada));

        assertEquals("Comisión no encontrada", excepcion.getMessage());
        verify(comisionRepository, times(1)).findById(comisionIdInexistente);
        verify(comisionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería eliminar una comisión correctamente")
    void deberiaEliminarComisionCorrectamente() {
        Long comisionId = 1L;
        when(comisionRepository.findById(comisionId)).thenReturn(Optional.of(comisionEjemplo));

        assertDoesNotThrow(() -> comisionService.eliminarComision(comisionId));

        verify(comisionRepository, times(1)).findById(comisionId);
        verify(comisionRepository, times(1)).delete(comisionEjemplo);
    }

    @Test
    @DisplayName("Debería lanzar EntityNotFoundException al eliminar comisión inexistente")
    void deberiaLanzarExcepcionAlEliminarComisionInexistente() {
        Long comisionIdInexistente = 999L;
        when(comisionRepository.findById(comisionIdInexistente)).thenReturn(Optional.empty());

        EntityNotFoundException excepcion = assertThrows(
                EntityNotFoundException.class,
                () -> comisionService.eliminarComision(comisionIdInexistente));

        assertEquals("Comisión no encontrada", excepcion.getMessage());
        verify(comisionRepository, times(1)).findById(comisionIdInexistente);
        verify(comisionRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Debería manejar lista vacía al obtener todas las comisiones")
    void deberiaManejarListaVaciaAlObtenerTodasLasComisiones() {
        when(comisionRepository.findAll()).thenReturn(Arrays.asList());

        List<Comision> resultado = comisionService.obtenerTodasLasComisiones();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(comisionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería manejar múltiples comisiones correctamente")
    void deberiaManejarMultiplesComisionesCorrectamente() {
        Comision comision2 = new Comision();
        comision2.setId(2L);
        comision2.setPagoId(101L);
        comision2.setTasa(8.0);
        comision2.setBase(500.0);
        comision2.setMonto(40.0);
        comision2.setFecha(LocalDate.now());

        Comision comision3 = new Comision();
        comision3.setId(3L);
        comision3.setPagoId(102L);
        comision3.setTasa(9.5);
        comision3.setBase(700.0);
        comision3.setMonto(66.5);
        comision3.setFecha(LocalDate.now());

        List<Comision> comisionesEsperadas = Arrays.asList(comisionEjemplo, comision2, comision3);
        when(comisionRepository.findAll()).thenReturn(comisionesEsperadas);

        List<Comision> resultado = comisionService.obtenerTodasLasComisiones();

        assertNotNull(resultado);
        assertEquals(3, resultado.size());
        assertEquals(100L, resultado.get(0).getPagoId());
        assertEquals(101L, resultado.get(1).getPagoId());
        assertEquals(102L, resultado.get(2).getPagoId());
        verify(comisionRepository, times(1)).findAll();
    }
}
