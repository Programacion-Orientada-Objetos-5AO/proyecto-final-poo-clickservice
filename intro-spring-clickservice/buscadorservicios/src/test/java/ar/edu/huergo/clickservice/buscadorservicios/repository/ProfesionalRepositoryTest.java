package ar.edu.huergo.clickservice.buscadorservicios.repository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import ar.edu.huergo.clickservice.buscadorservicios.entity.Profesional;
import ar.edu.huergo.clickservice.buscadorservicios.entity.Servicio;
import ar.edu.huergo.clickservice.buscadorservicios.entity.security.Rol;
import ar.edu.huergo.clickservice.buscadorservicios.entity.security.Usuario;

@DataJpaTest
@DisplayName("Tests de Integración - ProfesionalRepository")
class ProfesionalRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProfesionalRepository profesionalRepository;

    private Profesional profesional1;
    private Profesional profesional2;
    private Profesional profesional3;
    private Usuario usuario1;
    private Usuario usuario2;
    private Usuario usuario3;
    private Servicio servicio1;
    private Servicio servicio2;
    private Rol rolProfesional;

    @BeforeEach
    void setUp() {
        // Crear rol profesional
        rolProfesional = new Rol("PROFESIONAL");
        rolProfesional = entityManager.persistAndFlush(rolProfesional);

        // Crear usuarios de prueba
        usuario1 = new Usuario();
        usuario1.setUsername("juan.perez@clickservice.com");
        usuario1.setPassword("$2a$10$encodedPassword1");
        usuario1.setRoles(Set.of(rolProfesional));
        usuario1 = entityManager.persistAndFlush(usuario1);

        usuario2 = new Usuario();
        usuario2.setUsername("maria.garcia@clickservice.com");
        usuario2.setPassword("$2a$10$encodedPassword2");
        usuario2.setRoles(Set.of(rolProfesional));
        usuario2 = entityManager.persistAndFlush(usuario2);

        usuario3 = new Usuario();
        usuario3.setUsername("carlos.lopez@clickservice.com");
        usuario3.setPassword("$2a$10$encodedPassword3");
        usuario3.setRoles(Set.of(rolProfesional));
        usuario3 = entityManager.persistAndFlush(usuario3);

        // Crear servicios de prueba
        servicio1 = new Servicio();
        servicio1.setNombre("Plomería");
        servicio1.setPrecioHora(25.50);
        servicio1 = entityManager.persistAndFlush(servicio1);

        servicio2 = new Servicio();
        servicio2.setNombre("Electricidad");
        servicio2.setPrecioHora(30.00);
        servicio2 = entityManager.persistAndFlush(servicio2);

        // Crear profesionales de prueba
        profesional1 = new Profesional();
        profesional1.setUsuario(usuario1);
        profesional1.setNombreCompleto("Juan Carlos Pérez");
        profesional1.setTelefono("11-2345-6789");
        profesional1.setDescripcion("Plomero con 10 años de experiencia en CABA y GBA");
        profesional1.setCalificacionPromedio(4.5);
        profesional1.setTrabajosRealizados(25);
        profesional1.setDisponible(true);
        profesional1.setZonaTrabajo("Zona Norte - Vicente López, San Isidro");
        profesional1.setFechaRegistro(LocalDateTime.now());
        
        Set<Servicio> servicios1 = new HashSet<>();
        servicios1.add(servicio1);
        profesional1.setServicios(servicios1);
        profesional1 = entityManager.persistAndFlush(profesional1);

        profesional2 = new Profesional();
        profesional2.setUsuario(usuario2);
        profesional2.setNombreCompleto("María García López");
        profesional2.setTelefono("11-9876-5432");
        profesional2.setDescripcion("Electricista certificada con matrícula profesional");
        profesional2.setCalificacionPromedio(4.8);
        profesional2.setTrabajosRealizados(40);
        profesional2.setDisponible(true);
        profesional2.setZonaTrabajo("Zona Sur - Lomas de Zamora, Banfield");
        profesional2.setFechaRegistro(LocalDateTime.now());
        
        Set<Servicio> servicios2 = new HashSet<>();
        servicios2.add(servicio2);
        profesional2.setServicios(servicios2);
        profesional2 = entityManager.persistAndFlush(profesional2);

        profesional3 = new Profesional();
        profesional3.setUsuario(usuario3);
        profesional3.setNombreCompleto("Carlos López Méndez");
        profesional3.setTelefono("11-5555-1234");
        profesional3.setDescripcion("Servicios múltiples de hogar y mantenimiento");
        profesional3.setCalificacionPromedio(4.0);
        profesional3.setTrabajosRealizados(15);
        profesional3.setDisponible(false); // No disponible temporalmente
        profesional3.setZonaTrabajo("Centro - Microcentro, Retiro");
        profesional3.setFechaRegistro(LocalDateTime.now());
        
        Set<Servicio> servicios3 = new HashSet<>();
        servicios3.add(servicio1);
        servicios3.add(servicio2);
        profesional3.setServicios(servicios3);
        profesional3 = entityManager.persistAndFlush(profesional3);

        entityManager.clear();
    }

    @Test
    @DisplayName("Debería guardar y recuperar profesional correctamente")
    void deberiaGuardarYRecuperarProfesional() {
        // Given
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setUsername("ana.martinez@clickservice.com");
        nuevoUsuario.setPassword("$2a$10$encodedPasswordNew");
        nuevoUsuario.setRoles(Set.of(rolProfesional));
        nuevoUsuario = entityManager.persistAndFlush(nuevoUsuario);

        Profesional nuevoProfesional = new Profesional();
        nuevoProfesional.setUsuario(nuevoUsuario);
        nuevoProfesional.setNombreCompleto("Ana Sofía Martínez");
        nuevoProfesional.setTelefono("11-7777-8888");
        nuevoProfesional.setDescripcion("Jardinera profesional especializada en diseño de espacios verdes");
        nuevoProfesional.setCalificacionPromedio(4.2);
        nuevoProfesional.setTrabajosRealizados(8);
        nuevoProfesional.setDisponible(true);
        nuevoProfesional.setZonaTrabajo("Zona Oeste - Morón, Hurlingham");

        // When
        Profesional profesionalGuardado = profesionalRepository.save(nuevoProfesional);
        entityManager.flush();
        entityManager.clear();

        // Then
        assertNotNull(profesionalGuardado.getId());

        Optional<Profesional> profesionalRecuperado = 
                profesionalRepository.findById(profesionalGuardado.getId());

        assertTrue(profesionalRecuperado.isPresent());
        assertEquals("Ana Sofía Martínez", profesionalRecuperado.get().getNombreCompleto());
        assertEquals("11-7777-8888", profesionalRecuperado.get().getTelefono());
        assertEquals("Jardinera profesional especializada en diseño de espacios verdes", 
                profesionalRecuperado.get().getDescripcion());
        assertEquals(4.2, profesionalRecuperado.get().getCalificacionPromedio());
        assertEquals(8, profesionalRecuperado.get().getTrabajosRealizados());
        assertTrue(profesionalRecuperado.get().getDisponible());
        assertEquals("ana.martinez@clickservice.com", 
                profesionalRecuperado.get().getUsuario().getUsername());
    }

    @Test
    @DisplayName("Debería actualizar profesional existente")
    void deberiaActualizarProfesionalExistente() {
        // Given
        String nuevoNombre = "Juan Carlos Pérez Especialista";
        String nuevaDescripcion = "Plomero master con 15 años de experiencia y certificación gas";
        Double nuevaCalificacion = 4.9;
        String nuevaZona = "Zona Norte ampliada - Tigre, San Fernando";

        // When
        Optional<Profesional> profesionalOptional = 
                profesionalRepository.findById(profesional1.getId());
        assertTrue(profesionalOptional.isPresent());

        Profesional profesional = profesionalOptional.get();
        profesional.setNombreCompleto(nuevoNombre);
        profesional.setDescripcion(nuevaDescripcion);
        profesional.setCalificacionPromedio(nuevaCalificacion);
        profesional.setZonaTrabajo(nuevaZona);

        Profesional profesionalActualizado = profesionalRepository.save(profesional);
        entityManager.flush();

        // Then
        assertEquals(nuevoNombre, profesionalActualizado.getNombreCompleto());
        assertEquals(nuevaDescripcion, profesionalActualizado.getDescripcion());
        assertEquals(nuevaCalificacion, profesionalActualizado.getCalificacionPromedio());
        assertEquals(nuevaZona, profesionalActualizado.getZonaTrabajo());

        // Verificar persistencia
        entityManager.clear();
        Optional<Profesional> profesionalVerificacion = 
                profesionalRepository.findById(profesional1.getId());
        assertTrue(profesionalVerificacion.isPresent());
        assertEquals(nuevoNombre, profesionalVerificacion.get().getNombreCompleto());
        assertEquals(nuevaDescripcion, profesionalVerificacion.get().getDescripcion());
    }

    @Test
    @DisplayName("Debería eliminar profesional correctamente")
    void deberiaEliminarProfesional() {
        // Given
        Long profesionalId = profesional1.getId();
        assertTrue(profesionalRepository.existsById(profesionalId));

        // When
        profesionalRepository.deleteById(profesionalId);
        entityManager.flush();

        // Then
        assertFalse(profesionalRepository.existsById(profesionalId));
        Optional<Profesional> profesionalEliminado = profesionalRepository.findById(profesionalId);
        assertFalse(profesionalEliminado.isPresent());
    }

    @Test
    @DisplayName("Debería encontrar todos los profesionales")
    void deberiaEncontrarTodosLosProfesionales() {
        // When
        List<Profesional> todosLosProfesionales = profesionalRepository.findAll();

        // Then
        assertNotNull(todosLosProfesionales);
        assertEquals(3, todosLosProfesionales.size());

        List<String> nombres = todosLosProfesionales.stream()
                .map(Profesional::getNombreCompleto).toList();
        assertTrue(nombres.contains("Juan Carlos Pérez"));
        assertTrue(nombres.contains("María García López"));
        assertTrue(nombres.contains("Carlos López Méndez"));
    }

    @Test
    @DisplayName("Debería encontrar profesional por ID de usuario")
    void deberiaEncontrarProfesionalPorIdUsuario() {
        // When
        Optional<Profesional> profesionalEncontrado = 
                profesionalRepository.findByUsuarioId(usuario1.getId());

        // Then
        assertTrue(profesionalEncontrado.isPresent());
        assertEquals("Juan Carlos Pérez", profesionalEncontrado.get().getNombreCompleto());
        assertEquals(usuario1.getId(), profesionalEncontrado.get().getUsuario().getId());
        assertEquals("juan.perez@clickservice.com", 
                profesionalEncontrado.get().getUsuario().getUsername());
        assertTrue(profesionalEncontrado.get().getUsuario().getRoles().stream()
                .anyMatch(rol -> rol.getNombre().equals("PROFESIONAL")));
    }

    @Test
    @DisplayName("Debería retornar Optional vacío para usuario ID inexistente")
    void deberiaRetornarOptionalVacioParaUsuarioIdInexistente() {
        // Given
        Long usuarioIdInexistente = 999L;

        // When
        Optional<Profesional> profesionalEncontrado = 
                profesionalRepository.findByUsuarioId(usuarioIdInexistente);

        // Then
        assertFalse(profesionalEncontrado.isPresent());
    }

    @Test
    @DisplayName("Debería encontrar profesionales disponibles")
    void deberiaEncontrarProfesionalesDisponibles() {
        // When
        List<Profesional> profesionalesDisponibles = 
                profesionalRepository.findByDisponibleTrue();

        // Then
        assertNotNull(profesionalesDisponibles);
        assertEquals(2, profesionalesDisponibles.size()); // Solo profesional1 y profesional2 están disponibles

        List<String> nombres = profesionalesDisponibles.stream()
                .map(Profesional::getNombreCompleto).toList();
        assertTrue(nombres.contains("Juan Carlos Pérez"));
        assertTrue(nombres.contains("María García López"));
        assertFalse(nombres.contains("Carlos López Méndez")); // Este no está disponible

        // Verificar que todos los encontrados están disponibles
        profesionalesDisponibles.forEach(prof -> 
                assertTrue(prof.getDisponible(), "Todos los profesionales encontrados deben estar disponibles"));
    }

    @Test
    @DisplayName("Debería encontrar profesionales por servicio y disponibles")
    void deberiaEncontrarProfesionalesPorServicioYDisponibles() {
        // When - Buscar profesionales disponibles que ofrezcan servicio de plomería
        List<Profesional> profesionalesPlomeria = 
                profesionalRepository.findByServiciosIdAndDisponibleTrue(servicio1.getId());

        // Then
        assertNotNull(profesionalesPlomeria);
        assertEquals(1, profesionalesPlomeria.size()); // Solo profesional1 (profesional3 no está disponible)
        assertEquals("Juan Carlos Pérez", profesionalesPlomeria.get(0).getNombreCompleto());
        assertTrue(profesionalesPlomeria.get(0).getDisponible());
        assertTrue(profesionalesPlomeria.get(0).getServicios().stream()
                .anyMatch(servicio -> servicio.getId().equals(servicio1.getId())));

        // When - Buscar profesionales disponibles que ofrezcan servicio de electricidad
        List<Profesional> profesionalesElectricidad = 
                profesionalRepository.findByServiciosIdAndDisponibleTrue(servicio2.getId());

        // Then
        assertEquals(1, profesionalesElectricidad.size()); // Solo profesional2
        assertEquals("María García López", profesionalesElectricidad.get(0).getNombreCompleto());
        assertTrue(profesionalesElectricidad.get(0).getDisponible());
        assertTrue(profesionalesElectricidad.get(0).getServicios().stream()
                .anyMatch(servicio -> servicio.getId().equals(servicio2.getId())));
    }

    @Test
    @DisplayName("Debería retornar lista vacía para servicio sin profesionales disponibles")
    void deberiaRetornarListaVaciaParaServicioSinProfesionalesDisponibles() {
        // Given - Crear un servicio que no tiene profesionales asociados
        Servicio servicioSinProfesionales = new Servicio();
        servicioSinProfesionales.setNombre("Jardinería");
        servicioSinProfesionales.setPrecioHora(20.00);
        servicioSinProfesionales = entityManager.persistAndFlush(servicioSinProfesionales);

        // When
        List<Profesional> profesionales = 
                profesionalRepository.findByServiciosIdAndDisponibleTrue(servicioSinProfesionales.getId());

        // Then
        assertNotNull(profesionales);
        assertTrue(profesionales.isEmpty());
    }

    @Test
    @DisplayName("Debería contar profesionales correctamente")
    void deberiaContarProfesionales() {
        // When
        long cantidadProfesionales = profesionalRepository.count();

        // Then
        assertEquals(3, cantidadProfesionales);

        // Agregar un profesional más y verificar
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setUsername("roberto.silva@clickservice.com");
        nuevoUsuario.setPassword("$2a$10$encodedPasswordRoberto");
        nuevoUsuario.setRoles(Set.of(rolProfesional));
        nuevoUsuario = entityManager.persistAndFlush(nuevoUsuario);

        Profesional nuevoProfesional = new Profesional();
        nuevoProfesional.setUsuario(nuevoUsuario);
        nuevoProfesional.setNombreCompleto("Roberto Silva");
        nuevoProfesional.setTelefono("11-9999-0000");
        nuevoProfesional.setDescripcion("Técnico en aire acondicionado");
        entityManager.persistAndFlush(nuevoProfesional);

        assertEquals(4, profesionalRepository.count());
    }

    @Test
    @DisplayName("Debería validar restricciones de la entidad")
    void deberiaValidarRestricciones() {
        // Given - Crear profesional con nombre vacío
        Usuario usuarioTest = new Usuario();
        usuarioTest.setUsername("test.user@clickservice.com");
        usuarioTest.setPassword("$2a$10$encodedPasswordTest");
        usuarioTest.setRoles(Set.of(rolProfesional));
        usuarioTest = entityManager.persistAndFlush(usuarioTest);

        Profesional profesionalInvalido = new Profesional();
        profesionalInvalido.setUsuario(usuarioTest);
        profesionalInvalido.setNombreCompleto(""); // Viola @NotBlank
        profesionalInvalido.setTelefono("11-1234-5678");

        // When & Then
        assertThrows(Exception.class, () -> {
            entityManager.persistAndFlush(profesionalInvalido);
        });
    }

    @Test
    @DisplayName("Debería validar restricción de teléfono null")
    void deberiaValidarRestriccionTelefonoNull() {
        // Given - Crear profesional con teléfono null
        Usuario usuarioTest = new Usuario();
        usuarioTest.setUsername("test2.user@clickservice.com");
        usuarioTest.setPassword("$2a$10$encodedPasswordTest2");
        usuarioTest.setRoles(Set.of(rolProfesional));
        usuarioTest = entityManager.persistAndFlush(usuarioTest);

        Profesional profesionalInvalido = new Profesional();
        profesionalInvalido.setUsuario(usuarioTest);
        profesionalInvalido.setNombreCompleto("Test User");
        profesionalInvalido.setTelefono(null); // Viola @NotBlank

        // When & Then
        assertThrows(Exception.class, () -> {
            entityManager.persistAndFlush(profesionalInvalido);
        });
    }

    @Test
    @DisplayName("Debería validar restricción de usuario único")
    void deberiaValidarRestriccionUsuarioUnico() {
        // Given - Intentar crear dos profesionales con el mismo usuario
        Profesional profesionalDuplicado = new Profesional();
        profesionalDuplicado.setUsuario(usuario1); // Usuario ya usado por profesional1
        profesionalDuplicado.setNombreCompleto("Otro Profesional");
        profesionalDuplicado.setTelefono("11-0000-1111");

        // When & Then
        assertThrows(Exception.class, () -> {
            entityManager.persistAndFlush(profesionalDuplicado);
        });
    }

    @Test
    @DisplayName("Debería encontrar profesional por ID específico")
    void deberiaEncontrarProfesionalPorIdEspecifico() {
        // When
        Optional<Profesional> profesionalEncontrado = 
                profesionalRepository.findById(profesional2.getId());

        // Then
        assertTrue(profesionalEncontrado.isPresent());
        assertEquals("María García López", profesionalEncontrado.get().getNombreCompleto());
        assertEquals("11-9876-5432", profesionalEncontrado.get().getTelefono());
        assertEquals(4.8, profesionalEncontrado.get().getCalificacionPromedio());
        assertEquals("maria.garcia@clickservice.com", 
                profesionalEncontrado.get().getUsuario().getUsername());
    }

    @Test
    @DisplayName("Debería retornar Optional vacío para ID inexistente")
    void deberiaRetornarOptionalVacioParaIdInexistente() {
        // Given
        Long idInexistente = 999L;

        // When
        Optional<Profesional> profesionalEncontrado = profesionalRepository.findById(idInexistente);

        // Then
        assertFalse(profesionalEncontrado.isPresent());
    }

    @Test
    @DisplayName("Debería verificar existencia de profesional por ID")
    void deberiaVerificarExistenciaProfesionalPorId() {
        // When & Then
        assertTrue(profesionalRepository.existsById(profesional1.getId()));
        assertTrue(profesionalRepository.existsById(profesional2.getId()));
        assertTrue(profesionalRepository.existsById(profesional3.getId()));
        assertFalse(profesionalRepository.existsById(999L));
    }

    @Test
    @DisplayName("Debería eliminar profesional por entidad")
    void deberiaEliminarProfesionalPorEntidad() {
        // Given
        Long profesionalId = profesional3.getId();
        assertTrue(profesionalRepository.existsById(profesionalId));

        // When
        profesionalRepository.delete(profesional3);
        entityManager.flush();

        // Then
        assertFalse(profesionalRepository.existsById(profesionalId));
    }

    @Test
    @DisplayName("Debería manejar profesionales con calificaciones decimales")
    void deberiaManejarProfesionalesConCalificacionesDecimales() {
        // Given
        Usuario usuarioTest = new Usuario();
        usuarioTest.setUsername("decimal.test@clickservice.com");
        usuarioTest.setPassword("$2a$10$encodedPasswordDecimal");
        usuarioTest.setRoles(Set.of(rolProfesional));
        usuarioTest = entityManager.persistAndFlush(usuarioTest);

        Profesional profesionalConDecimales = new Profesional();
        profesionalConDecimales.setUsuario(usuarioTest);
        profesionalConDecimales.setNombreCompleto("Test Decimal Calificación");
        profesionalConDecimales.setTelefono("11-0000-1111");
        profesionalConDecimales.setCalificacionPromedio(4.75);
        profesionalConDecimales.setTrabajosRealizados(33);
        profesionalConDecimales.setDescripcion("Profesional con calificación decimal");

        // When
        Profesional profesionalGuardado = profesionalRepository.save(profesionalConDecimales);
        entityManager.flush();
        entityManager.clear();

        Optional<Profesional> profesionalRecuperado = 
                profesionalRepository.findById(profesionalGuardado.getId());

        // Then
        assertTrue(profesionalRecuperado.isPresent());
        assertEquals(4.75, profesionalRecuperado.get().getCalificacionPromedio());
        assertEquals(33, profesionalRecuperado.get().getTrabajosRealizados());
    }

    @Test
    @DisplayName("Debería manejar relación con servicios correctamente")
    void deberiaManejarRelacionConServiciosCorrectamente() {
        // When
        Optional<Profesional> profesionalConServicios = 
                profesionalRepository.findById(profesional3.getId());

        // Then
        assertTrue(profesionalConServicios.isPresent());
        assertNotNull(profesionalConServicios.get().getServicios());
        assertEquals(2, profesionalConServicios.get().getServicios().size());
        
        Set<String> nombresServicios = profesionalConServicios.get().getServicios().stream()
                .map(Servicio::getNombre)
                .collect(java.util.stream.Collectors.toSet());
        
        assertTrue(nombresServicios.contains("Plomería"));
        assertTrue(nombresServicios.contains("Electricidad"));
    }

    @Test
    @DisplayName("Debería manejar relación con usuario y roles correctamente")
    void deberiaManejarRelacionConUsuarioYRolesCorrectamente() {
        // When
        Optional<Profesional> profesionalConUsuario = 
                profesionalRepository.findById(profesional1.getId());

        // Then
        assertTrue(profesionalConUsuario.isPresent());
        assertNotNull(profesionalConUsuario.get().getUsuario());
        assertEquals("juan.perez@clickservice.com", 
                profesionalConUsuario.get().getUsuario().getUsername());
        assertNotNull(profesionalConUsuario.get().getUsuario().getRoles());
        assertFalse(profesionalConUsuario.get().getUsuario().getRoles().isEmpty());
        assertTrue(profesionalConUsuario.get().getUsuario().getRoles().stream()
                .anyMatch(rol -> rol.getNombre().equals("PROFESIONAL")));
    }

    @Test
    @DisplayName("Debería manejar fechas de registro correctamente")
    void deberiaManejarFechasRegistroCorrectamente() {
        // When
        List<Profesional> profesionales = profesionalRepository.findAll();

        // Then
        profesionales.forEach(profesional -> {
            assertNotNull(profesional.getFechaRegistro(), 
                    "Todos los profesionales deben tener fecha de registro");
            assertTrue(profesional.getFechaRegistro().isBefore(LocalDateTime.now().plusMinutes(1)),
                    "La fecha de registro debe ser anterior o igual al momento actual");
        });
    }

    @Test
    @DisplayName("Debería manejar profesionales con diferentes zonas de trabajo")
    void deberiaManejarProfesionalesConDiferentesZonasTrabajo() {
        // When
        List<Profesional> profesionales = profesionalRepository.findAll();

        // Then
        List<String> zonas = profesionales.stream()
                .map(Profesional::getZonaTrabajo)
                .toList();
        
        assertTrue(zonas.contains("Zona Norte - Vicente López, San Isidro"));
        assertTrue(zonas.contains("Zona Sur - Lomas de Zamora, Banfield"));
        assertTrue(zonas.contains("Centro - Microcentro, Retiro"));
        
        // Verificar que no hay zonas duplicadas exactas
        assertEquals(3, zonas.stream().distinct().count());
    }
}