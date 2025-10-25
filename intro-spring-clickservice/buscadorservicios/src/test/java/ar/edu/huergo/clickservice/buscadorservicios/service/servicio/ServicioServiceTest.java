package ar.edu.huergo.clickservice.buscadorservicios.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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

import ar.edu.huergo.clickservice.buscadorservicios.entity.Servicio;
import ar.edu.huergo.clickservice.buscadorservicios.repository.ServicioRepository;
import jakarta.persistence.EntityNotFoundException;

/**
 * Tests de unidad para ServicioService
 * 
 * CONCEPTOS DEMOSTRADOS:
 * 1. @ExtendWith(MockitoExtension.class) - Habilita el uso de Mockito en JUnit 5
 * 2. @Mock - Crea objetos mock (simulados) de las dependencias
 * 3. @InjectMocks - Inyecta los mocks en la clase bajo prueba
 * 4. @BeforeEach - Método que se ejecuta antes de cada test
 * 5. @DisplayName - Nombres descriptivos para los tests
 * 6. when().thenReturn() - Define el comportamiento de los mocks
 * 7. verify() - Verifica que se llamaron métodos específicos
 * 8. Assertions - Verificaciones del resultado esperado
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de Unidad - ServicioService")
class ServicioServiceTest {

    @Mock
    private ServicioRepository servicioRepository;

    @InjectMocks
    private ServicioService servicioService;

    private Servicio servicioEjemplo;

    @BeforeEach // Se ejecuta antes de cada test
    void setUp() {
        // Preparación de datos de prueba
        servicioEjemplo = new Servicio();
        servicioEjemplo.setId(1L);
        servicioEjemplo.setNombre("Plomería");
        servicioEjemplo.setPrecioHora(25.50);
    }

    @Test
    @DisplayName("Debería obtener todos los servicios correctamente")
    void deberiaObtenerTodosLosServicios() {
        // Given - Preparación
        List<Servicio> serviciosEsperados = Arrays.asList(servicioEjemplo);
        when(servicioRepository.findAll()).thenReturn(serviciosEsperados);

        // When - Ejecución
        List<Servicio> resultado = servicioService.obtenerTodosLosServicios();

        // Then - Verificación
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(servicioEjemplo.getNombre(), resultado.get(0).getNombre());
        assertEquals(servicioEjemplo.getPrecioHora(), resultado.get(0).getPrecioHora());

        // Verificar que se llamó al método del repositorio
        verify(servicioRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería obtener un servicio por ID cuando existe")
    void deberiaObtenerServicioPorIdCuandoExiste() {
        // Given
        Long servicioId = 1L;
        when(servicioRepository.findById(servicioId)).thenReturn(Optional.of(servicioEjemplo));

        // When
        Servicio resultado = servicioService.obtenerServicioPorId(servicioId);

        // Then
        assertNotNull(resultado);
        assertEquals(servicioEjemplo.getId(), resultado.getId());
        assertEquals(servicioEjemplo.getNombre(), resultado.getNombre());
        assertEquals(servicioEjemplo.getPrecioHora(), resultado.getPrecioHora());
        verify(servicioRepository, times(1)).findById(servicioId);
    }

    @Test
    @DisplayName("Debería lanzar EntityNotFoundException cuando el servicio no existe")
    void deberiaLanzarExcepcionCuandoServicioNoExiste() {
        // Given
        Long servicioIdInexistente = 999L;
        when(servicioRepository.findById(servicioIdInexistente)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException excepcion = assertThrows(
                EntityNotFoundException.class,
                () -> servicioService.obtenerServicioPorId(servicioIdInexistente));

        assertEquals("Servicio no encontrado", excepcion.getMessage());
        verify(servicioRepository, times(1)).findById(servicioIdInexistente);
    }

    @Test
    @DisplayName("Debería crear un servicio correctamente")
    void deberiaCrearServicioCorrectamente() {
        // Given
        Servicio nuevoServicio = new Servicio();
        nuevoServicio.setNombre("Electricidad");
        nuevoServicio.setPrecioHora(30.00);

        when(servicioRepository.save(nuevoServicio)).thenReturn(nuevoServicio);

        // When
        Servicio resultado = servicioService.crearServicio(nuevoServicio);

        // Then
        assertNotNull(resultado);
        assertEquals(nuevoServicio.getNombre(), resultado.getNombre());
        assertEquals(nuevoServicio.getPrecioHora(), resultado.getPrecioHora());

        verify(servicioRepository, times(1)).save(nuevoServicio);
    }

    @Test
    @DisplayName("Debería actualizar un servicio existente correctamente")
    void deberiaActualizarServicioExistente() {
        // Given
        Long servicioId = 1L;
        Servicio servicioActualizado = new Servicio();
        servicioActualizado.setNombre("Plomería Especializada");
        servicioActualizado.setPrecioHora(35.00);

        when(servicioRepository.findById(servicioId)).thenReturn(Optional.of(servicioEjemplo));
        when(servicioRepository.save(any(Servicio.class))).thenReturn(servicioEjemplo);

        // When
        Servicio resultado = servicioService.actualizarServicio(servicioId, servicioActualizado);

        // Then
        assertNotNull(resultado);
        verify(servicioRepository, times(1)).findById(servicioId);
        verify(servicioRepository, times(1)).save(servicioEjemplo);

        // Verificar que los campos se actualizaron
        assertEquals(servicioActualizado.getNombre(), servicioEjemplo.getNombre());
        assertEquals(servicioActualizado.getPrecioHora(), servicioEjemplo.getPrecioHora());
    }

    @Test
    @DisplayName("Debería lanzar EntityNotFoundException al actualizar servicio inexistente")
    void deberiaLanzarExcepcionAlActualizarServicioInexistente() {
        // Given
        Long servicioIdInexistente = 999L;
        Servicio servicioActualizado = new Servicio();
        servicioActualizado.setNombre("Gas");
        servicioActualizado.setPrecioHora(28.00);

        when(servicioRepository.findById(servicioIdInexistente)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException excepcion = assertThrows(
                EntityNotFoundException.class,
                () -> servicioService.actualizarServicio(servicioIdInexistente, servicioActualizado));

        assertEquals("Servicio no encontrado", excepcion.getMessage());
        verify(servicioRepository, times(1)).findById(servicioIdInexistente);
        verify(servicioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería eliminar un servicio correctamente")
    void deberiaEliminarServicioCorrectamente() {
        // Given
        Long servicioId = 1L;
        when(servicioRepository.findById(servicioId)).thenReturn(Optional.of(servicioEjemplo));

        // When
        assertDoesNotThrow(() -> servicioService.eliminarServicio(servicioId));

        // Then
        verify(servicioRepository, times(1)).findById(servicioId);
        verify(servicioRepository, times(1)).delete(servicioEjemplo);
    }

    @Test
    @DisplayName("Debería lanzar EntityNotFoundException al eliminar servicio inexistente")
    void deberiaLanzarExcepcionAlEliminarServicioInexistente() {
        // Given
        Long servicioIdInexistente = 999L;
        when(servicioRepository.findById(servicioIdInexistente)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException excepcion = assertThrows(
                EntityNotFoundException.class,
                () -> servicioService.eliminarServicio(servicioIdInexistente));

        assertEquals("Servicio no encontrado", excepcion.getMessage());
        verify(servicioRepository, times(1)).findById(servicioIdInexistente);
        verify(servicioRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Debería manejar lista vacía al obtener todos los servicios")
    void deberiaManejarListaVaciaAlObtenerTodosLosServicios() {
        // Given
        when(servicioRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<Servicio> resultado = servicioService.obtenerTodosLosServicios();

        // Then
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(servicioRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería manejar múltiples servicios correctamente")
    void deberiaManejarMultiplesServiciosCorrectamente() {
        // Given
        Servicio servicio2 = new Servicio();
        servicio2.setId(2L);
        servicio2.setNombre("Electricidad");
        servicio2.setPrecioHora(30.00);

        Servicio servicio3 = new Servicio();
        servicio3.setId(3L);
        servicio3.setNombre("Carpintería");
        servicio3.setPrecioHora(20.75);

        List<Servicio> serviciosEsperados = Arrays.asList(servicioEjemplo, servicio2, servicio3);
        when(servicioRepository.findAll()).thenReturn(serviciosEsperados);

        // When
        List<Servicio> resultado = servicioService.obtenerTodosLosServicios();

        // Then
        assertNotNull(resultado);
        assertEquals(3, resultado.size());
        assertEquals("Plomería", resultado.get(0).getNombre());
        assertEquals("Electricidad", resultado.get(1).getNombre());
        assertEquals("Carpintería", resultado.get(2).getNombre());
        verify(servicioRepository, times(1)).findAll();
    }
}