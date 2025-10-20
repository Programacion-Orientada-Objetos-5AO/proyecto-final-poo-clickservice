package ar.edu.huergo.clickservice.buscadorservicios.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ar.edu.huergo.clickservice.buscadorservicios.entity.Profesional;
import ar.edu.huergo.clickservice.buscadorservicios.entity.Servicio;
import ar.edu.huergo.clickservice.buscadorservicios.entity.security.Rol;
import ar.edu.huergo.clickservice.buscadorservicios.entity.security.Usuario;
import ar.edu.huergo.clickservice.buscadorservicios.repository.ProfesionalRepository;
import ar.edu.huergo.clickservice.buscadorservicios.repository.ServicioRepository;
import jakarta.persistence.EntityNotFoundException;

/**
 * Tests de unidad para ProfesionalService
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
 * 9. Testing de relaciones complejas (Usuario, Servicios, Roles)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de Unidad - ProfesionalService")
class ProfesionalServiceTest {

    @Mock
    private ProfesionalRepository profesionalRepository;

    @Mock
    private ServicioRepository servicioRepository;

    @InjectMocks
    private ProfesionalService profesionalService;

    private Profesional profesionalEjemplo;
    private Usuario usuarioEjemplo;
    private Servicio servicioEjemplo1;
    private Servicio servicioEjemplo2;
    private Rol rolProfesional;

    @BeforeEach // Se ejecuta antes de cada test
    void setUp() {
        // Preparación de datos de prueba
        
        // Crear rol
        rolProfesional = new Rol("PROFESIONAL");
        rolProfesional.setId(1L);

        // Crear usuario
        usuarioEjemplo = new Usuario();
        usuarioEjemplo.setId(1L);
        usuarioEjemplo.setUsername("juan.perez@clickservice.com");
        usuarioEjemplo.setPassword("$2a$10$encodedPassword");
        usuarioEjemplo.setRoles(Set.of(rolProfesional));

        // Crear servicios
        servicioEjemplo1 = new Servicio();
        servicioEjemplo1.setId(1L);
        servicioEjemplo1.setNombre("Plomería");
        servicioEjemplo1.setPrecioHora(25.50);

        servicioEjemplo2 = new Servicio();
        servicioEjemplo2.setId(2L);
        servicioEjemplo2.setNombre("Electricidad");
        servicioEjemplo2.setPrecioHora(30.00);

        // Crear profesional
        profesionalEjemplo = new Profesional();
        profesionalEjemplo.setId(1L);
        profesionalEjemplo.setUsuario(usuarioEjemplo);
        profesionalEjemplo.setNombreCompleto("Juan Carlos Pérez");
        profesionalEjemplo.setTelefono("11-2345-6789");
        profesionalEjemplo.setDescripcion("Plomero con 10 años de experiencia");
        profesionalEjemplo.setCalificacionPromedio(4.5);
        profesionalEjemplo.setTrabajosRealizados(25);
        profesionalEjemplo.setDisponible(true);
        profesionalEjemplo.setZonaTrabajo("Zona Norte");
        profesionalEjemplo.setFechaRegistro(LocalDateTime.now());
        
        Set<Servicio> servicios = new HashSet<>();
        servicios.add(servicioEjemplo1);
        profesionalEjemplo.setServicios(servicios);
    }

    @Test
    @DisplayName("Debería obtener todos los profesionales correctamente")
    void deberiaObtenerTodosLosProfesionales() {
        // Given - Preparación
        List<Profesional> profesionalesEsperados = Arrays.asList(profesionalEjemplo);
        when(profesionalRepository.findAll()).thenReturn(profesionalesEsperados);

        // When - Ejecución
        List<Profesional> resultado = profesionalService.obtenerTodosLosProfesionales();

        // Then - Verificación
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(profesionalEjemplo.getNombreCompleto(), resultado.get(0).getNombreCompleto());
        assertEquals(profesionalEjemplo.getTelefono(), resultado.get(0).getTelefono());
        assertEquals(profesionalEjemplo.getCalificacionPromedio(), resultado.get(0).getCalificacionPromedio());

        // Verificar que se llamó al método del repositorio
        verify(profesionalRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería obtener un profesional por ID cuando existe")
    void deberiaObtenerProfesionalPorIdCuandoExiste() {
        // Given
        Long profesionalId = 1L;
        when(profesionalRepository.findById(profesionalId)).thenReturn(Optional.of(profesionalEjemplo));

        // When
        Profesional resultado = profesionalService.obtenerProfesionalPorId(profesionalId);

        // Then
        assertNotNull(resultado);
        assertEquals(profesionalEjemplo.getId(), resultado.getId());
        assertEquals(profesionalEjemplo.getNombreCompleto(), resultado.getNombreCompleto());
        assertEquals(profesionalEjemplo.getTelefono(), resultado.getTelefono());
        assertEquals(profesionalEjemplo.getUsuario().getUsername(), resultado.getUsuario().getUsername());
        verify(profesionalRepository, times(1)).findById(profesionalId);
    }

    @Test
    @DisplayName("Debería lanzar EntityNotFoundException cuando el profesional no existe")
    void deberiaLanzarExcepcionCuandoProfesionalNoExiste() {
        // Given
        Long profesionalIdInexistente = 999L;
        when(profesionalRepository.findById(profesionalIdInexistente)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException excepcion = assertThrows(
                EntityNotFoundException.class,
                () -> profesionalService.obtenerProfesionalPorId(profesionalIdInexistente));

        assertEquals("Profesional no encontrado", excepcion.getMessage());
        verify(profesionalRepository, times(1)).findById(profesionalIdInexistente);
    }

    @Test
    @DisplayName("Debería crear un profesional correctamente")
    void deberiaCrearProfesionalCorrectamente() {
        // Given
        Profesional nuevoProfesional = new Profesional();
        nuevoProfesional.setUsuario(usuarioEjemplo);
        nuevoProfesional.setNombreCompleto("María García López");
        nuevoProfesional.setTelefono("11-9876-5432");
        nuevoProfesional.setDescripcion("Electricista certificada");

        when(profesionalRepository.save(nuevoProfesional)).thenReturn(nuevoProfesional);

        // When
        Profesional resultado = profesionalService.crearProfesional(nuevoProfesional);

        // Then
        assertNotNull(resultado);
        assertEquals(nuevoProfesional.getNombreCompleto(), resultado.getNombreCompleto());
        assertEquals(nuevoProfesional.getTelefono(), resultado.getTelefono());
        assertEquals(nuevoProfesional.getDescripcion(), resultado.getDescripcion());

        verify(profesionalRepository, times(1)).save(nuevoProfesional);
    }

    @Test
    @DisplayName("Debería actualizar un profesional existente correctamente")
    void deberiaActualizarProfesionalExistente() {
        // Given
        Long profesionalId = 1L;
        Profesional profesionalActualizado = new Profesional();
        profesionalActualizado.setNombreCompleto("Juan Carlos Pérez Especialista");
        profesionalActualizado.setTelefono("11-2345-6789");
        profesionalActualizado.setDescripcion("Plomero master con 15 años de experiencia");
        profesionalActualizado.setDisponible(true);
        profesionalActualizado.setZonaTrabajo("Zona Norte ampliada");

        when(profesionalRepository.findById(profesionalId)).thenReturn(Optional.of(profesionalEjemplo));
        when(profesionalRepository.save(any(Profesional.class))).thenReturn(profesionalEjemplo);

        // When
        Profesional resultado = profesionalService.actualizarProfesional(profesionalId, profesionalActualizado);

        // Then
        assertNotNull(resultado);
        verify(profesionalRepository, times(1)).findById(profesionalId);
        verify(profesionalRepository, times(1)).save(profesionalEjemplo);

        // Verificar que los campos se actualizaron
        assertEquals(profesionalActualizado.getNombreCompleto(), profesionalEjemplo.getNombreCompleto());
        assertEquals(profesionalActualizado.getTelefono(), profesionalEjemplo.getTelefono());
        assertEquals(profesionalActualizado.getDescripcion(), profesionalEjemplo.getDescripcion());
        assertEquals(profesionalActualizado.getDisponible(), profesionalEjemplo.getDisponible());
        assertEquals(profesionalActualizado.getZonaTrabajo(), profesionalEjemplo.getZonaTrabajo());
    }

    @Test
    @DisplayName("Debería lanzar EntityNotFoundException al actualizar profesional inexistente")
    void deberiaLanzarExcepcionAlActualizarProfesionalInexistente() {
        // Given
        Long profesionalIdInexistente = 999L;
        Profesional profesionalActualizado = new Profesional();
        profesionalActualizado.setNombreCompleto("Carlos López");
        profesionalActualizado.setTelefono("11-5555-1234");

        when(profesionalRepository.findById(profesionalIdInexistente)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException excepcion = assertThrows(
                EntityNotFoundException.class,
                () -> profesionalService.actualizarProfesional(profesionalIdInexistente, profesionalActualizado));

        assertEquals("Profesional no encontrado", excepcion.getMessage());
        verify(profesionalRepository, times(1)).findById(profesionalIdInexistente);
        verify(profesionalRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería eliminar un profesional correctamente")
    void deberiaEliminarProfesionalCorrectamente() {
        // Given
        Long profesionalId = 1L;
        when(profesionalRepository.findById(profesionalId)).thenReturn(Optional.of(profesionalEjemplo));

        // When
        assertDoesNotThrow(() -> profesionalService.eliminarProfesional(profesionalId));

        // Then
        verify(profesionalRepository, times(1)).findById(profesionalId);
        verify(profesionalRepository, times(1)).delete(profesionalEjemplo);
    }

    @Test
    @DisplayName("Debería lanzar EntityNotFoundException al eliminar profesional inexistente")
    void deberiaLanzarExcepcionAlEliminarProfesionalInexistente() {
        // Given
        Long profesionalIdInexistente = 999L;
        when(profesionalRepository.findById(profesionalIdInexistente)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException excepcion = assertThrows(
                EntityNotFoundException.class,
                () -> profesionalService.eliminarProfesional(profesionalIdInexistente));

        assertEquals("Profesional no encontrado", excepcion.getMessage());
        verify(profesionalRepository, times(1)).findById(profesionalIdInexistente);
        verify(profesionalRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Debería obtener profesionales por servicio correctamente")
    void deberiaObtenerProfesionalesPorServicio() {
        // Given
        Long servicioId = 1L;
        List<Profesional> profesionalesEsperados = Arrays.asList(profesionalEjemplo);
        when(profesionalRepository.findByServiciosIdAndDisponibleTrue(servicioId))
                .thenReturn(profesionalesEsperados);

        // When
        List<Profesional> resultado = profesionalService.obtenerProfesionalesPorServicio(servicioId);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(profesionalEjemplo.getNombreCompleto(), resultado.get(0).getNombreCompleto());
        assertTrue(resultado.get(0).getDisponible());
        verify(profesionalRepository, times(1)).findByServiciosIdAndDisponibleTrue(servicioId);
    }

    @Test
    @DisplayName("Debería obtener profesionales disponibles correctamente")
    void deberiaObtenerProfesionalesDisponibles() {
        // Given
        List<Profesional> profesionalesDisponibles = Arrays.asList(profesionalEjemplo);
        when(profesionalRepository.findByDisponibleTrue()).thenReturn(profesionalesDisponibles);

        // When
        List<Profesional> resultado = profesionalService.obtenerProfesionalesDisponibles();

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).getDisponible());
        assertEquals(profesionalEjemplo.getNombreCompleto(), resultado.get(0).getNombreCompleto());
        verify(profesionalRepository, times(1)).findByDisponibleTrue();
    }

    @Test
    @DisplayName("Debería asignar servicios a profesional correctamente")
    void deberiaAsignarServiciosAProfesionalCorrectamente() {
        // Given
        Long profesionalId = 1L;
        Set<Long> serviciosIds = Set.of(1L, 2L);
        List<Servicio> serviciosEncontrados = Arrays.asList(servicioEjemplo1, servicioEjemplo2);

        when(profesionalRepository.findById(profesionalId)).thenReturn(Optional.of(profesionalEjemplo));
        when(servicioRepository.findAllById(serviciosIds)).thenReturn(serviciosEncontrados);
        when(profesionalRepository.save(any(Profesional.class))).thenReturn(profesionalEjemplo);

        // When
        Profesional resultado = profesionalService.asignarServicios(profesionalId, serviciosIds);

        // Then
        assertNotNull(resultado);
        verify(profesionalRepository, times(1)).findById(profesionalId);
        verify(servicioRepository, times(1)).findAllById(serviciosIds);
        verify(profesionalRepository, times(1)).save(profesionalEjemplo);

        // Verificar que se asignaron los servicios
        assertEquals(2, profesionalEjemplo.getServicios().size());
        assertTrue(profesionalEjemplo.getServicios().contains(servicioEjemplo1));
        assertTrue(profesionalEjemplo.getServicios().contains(servicioEjemplo2));
    }

    @Test
    @DisplayName("Debería lanzar EntityNotFoundException al asignar servicios a profesional inexistente")
    void deberiaLanzarExcepcionAlAsignarServiciosAProfesionalInexistente() {
        // Given
        Long profesionalIdInexistente = 999L;
        Set<Long> serviciosIds = Set.of(1L, 2L);

        when(profesionalRepository.findById(profesionalIdInexistente)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException excepcion = assertThrows(
                EntityNotFoundException.class,
                () -> profesionalService.asignarServicios(profesionalIdInexistente, serviciosIds));

        assertEquals("Profesional no encontrado", excepcion.getMessage());
        verify(profesionalRepository, times(1)).findById(profesionalIdInexistente);
        verify(servicioRepository, never()).findAllById(any());
        verify(profesionalRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería obtener profesional por usuario ID correctamente")
    void deberiaObtenerProfesionalPorUsuarioId() {
        // Given
        Long usuarioId = 1L;
        when(profesionalRepository.findByUsuarioId(usuarioId)).thenReturn(Optional.of(profesionalEjemplo));

        // When
        Profesional resultado = profesionalService.obtenerProfesionalPorUsuarioId(usuarioId);

        // Then
        assertNotNull(resultado);
        assertEquals(profesionalEjemplo.getId(), resultado.getId());
        assertEquals(profesionalEjemplo.getUsuario().getId(), resultado.getUsuario().getId());
        assertEquals(usuarioId, resultado.getUsuario().getId());
        verify(profesionalRepository, times(1)).findByUsuarioId(usuarioId);
    }

    @Test
    @DisplayName("Debería lanzar EntityNotFoundException al buscar profesional por usuario ID inexistente")
    void deberiaLanzarExcepcionAlBuscarProfesionalPorUsuarioIdInexistente() {
        // Given
        Long usuarioIdInexistente = 999L;
        when(profesionalRepository.findByUsuarioId(usuarioIdInexistente)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException excepcion = assertThrows(
                EntityNotFoundException.class,
                () -> profesionalService.obtenerProfesionalPorUsuarioId(usuarioIdInexistente));

        assertEquals("Profesional no encontrado", excepcion.getMessage());
        verify(profesionalRepository, times(1)).findByUsuarioId(usuarioIdInexistente);
    }

    @Test
    @DisplayName("Debería manejar lista vacía al obtener todos los profesionales")
    void deberiaManejarListaVaciaAlObtenerTodosLosProfesionales() {
        // Given
        when(profesionalRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<Profesional> resultado = profesionalService.obtenerTodosLosProfesionales();

        // Then
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(profesionalRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería manejar múltiples profesionales correctamente")
    void deberiaManejarMultiplesProfesionalesCorrectamente() {
        // Given
        Usuario usuario2 = new Usuario();
        usuario2.setId(2L);
        usuario2.setUsername("maria.garcia@clickservice.com");
        usuario2.setRoles(Set.of(rolProfesional));

        Profesional profesional2 = new Profesional();
        profesional2.setId(2L);
        profesional2.setUsuario(usuario2);
        profesional2.setNombreCompleto("María García López");
        profesional2.setTelefono("11-9876-5432");
        profesional2.setCalificacionPromedio(4.8);
        profesional2.setDisponible(true);

        Usuario usuario3 = new Usuario();
        usuario3.setId(3L);
        usuario3.setUsername("carlos.lopez@clickservice.com");
        usuario3.setRoles(Set.of(rolProfesional));

        Profesional profesional3 = new Profesional();
        profesional3.setId(3L);
        profesional3.setUsuario(usuario3);
        profesional3.setNombreCompleto("Carlos López Méndez");
        profesional3.setTelefono("11-5555-1234");
        profesional3.setCalificacionPromedio(4.0);
        profesional3.setDisponible(false);

        List<Profesional> profesionalesEsperados = Arrays.asList(profesionalEjemplo, profesional2, profesional3);
        when(profesionalRepository.findAll()).thenReturn(profesionalesEsperados);

        // When
        List<Profesional> resultado = profesionalService.obtenerTodosLosProfesionales();

        // Then
        assertNotNull(resultado);
        assertEquals(3, resultado.size());
        assertEquals("Juan Carlos Pérez", resultado.get(0).getNombreCompleto());
        assertEquals("María García López", resultado.get(1).getNombreCompleto());
        assertEquals("Carlos López Méndez", resultado.get(2).getNombreCompleto());
        verify(profesionalRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería manejar lista vacía al obtener profesionales por servicio")
    void deberiaManejarListaVaciaAlObtenerProfesionalesPorServicio() {
        // Given
        Long servicioId = 999L; // Servicio sin profesionales
        when(profesionalRepository.findByServiciosIdAndDisponibleTrue(servicioId))
                .thenReturn(Arrays.asList());

        // When
        List<Profesional> resultado = profesionalService.obtenerProfesionalesPorServicio(servicioId);

        // Then
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(profesionalRepository, times(1)).findByServiciosIdAndDisponibleTrue(servicioId);
    }

    @Test
    @DisplayName("Debería manejar lista vacía al obtener profesionales disponibles")
    void deberiaManejarListaVaciaAlObtenerProfesionalesDisponibles() {
        // Given
        when(profesionalRepository.findByDisponibleTrue()).thenReturn(Arrays.asList());

        // When
        List<Profesional> resultado = profesionalService.obtenerProfesionalesDisponibles();

        // Then
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(profesionalRepository, times(1)).findByDisponibleTrue();
    }

    @Test
    @DisplayName("Debería asignar servicios vacíos correctamente")
    void deberiaAsignarServiciosVaciosCorrectamente() {
        // Given
        Long profesionalId = 1L;
        Set<Long> serviciosIds = Set.of(); // Set vacío
        List<Servicio> serviciosEncontrados = Arrays.asList(); // Lista vacía

        when(profesionalRepository.findById(profesionalId)).thenReturn(Optional.of(profesionalEjemplo));
        when(servicioRepository.findAllById(serviciosIds)).thenReturn(serviciosEncontrados);
        when(profesionalRepository.save(any(Profesional.class))).thenReturn(profesionalEjemplo);

        // When
        Profesional resultado = profesionalService.asignarServicios(profesionalId, serviciosIds);

        // Then
        assertNotNull(resultado);
        verify(profesionalRepository, times(1)).findById(profesionalId);
        verify(servicioRepository, times(1)).findAllById(serviciosIds);
        verify(profesionalRepository, times(1)).save(profesionalEjemplo);

        // Verificar que se asignó un set vacío
        assertTrue(profesionalEjemplo.getServicios().isEmpty());
    }
} 