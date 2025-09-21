package ar.edu.huergo.clickservice.buscadorservicios.repository;

import java.util.List;
import java.util.Optional;

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

import ar.edu.huergo.clickservice.buscadorservicios.entity.Servicio;

@DataJpaTest
@DisplayName("Tests de Integración - ServicioRepository")
class ServicioRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ServicioRepository servicioRepository;

    private Servicio servicio1;
    private Servicio servicio2;
    private Servicio servicio3;

    @BeforeEach
    void setUp() {
        // Crear servicios de prueba
        servicio1 = new Servicio();
        servicio1.setNombre("Plomería");
        servicio1.setPrecioHora(25.50);
        servicio1 = entityManager.persistAndFlush(servicio1);

        servicio2 = new Servicio();
        servicio2.setNombre("Electricidad");
        servicio2.setPrecioHora(30.00);
        servicio2 = entityManager.persistAndFlush(servicio2);

        servicio3 = new Servicio();
        servicio3.setNombre("Carpintería");
        servicio3.setPrecioHora(20.75);
        servicio3 = entityManager.persistAndFlush(servicio3);

        entityManager.clear();
    }

    @Test
    @DisplayName("Debería guardar y recuperar servicio correctamente")
    void deberiaGuardarYRecuperarServicio() {
        // Given
        Servicio nuevoServicio = new Servicio();
        nuevoServicio.setNombre("Jardinería");
        nuevoServicio.setPrecioHora(18.50);

        // When
        Servicio servicioGuardado = servicioRepository.save(nuevoServicio);
        entityManager.flush();
        entityManager.clear();

        // Then
        assertNotNull(servicioGuardado.getId());

        Optional<Servicio> servicioRecuperado =
                servicioRepository.findById(servicioGuardado.getId());

        assertTrue(servicioRecuperado.isPresent());
        assertEquals("Jardinería", servicioRecuperado.get().getNombre());
        assertEquals(18.50, servicioRecuperado.get().getPrecioHora());
    }

    @Test
    @DisplayName("Debería actualizar servicio existente")
    void deberiaActualizarServicioExistente() {
        // Given
        String nuevoNombre = "Plomería Especializada";
        Double nuevoPrecio = 35.00;

        // When
        Optional<Servicio> servicioOptional =
                servicioRepository.findById(servicio1.getId());
        assertTrue(servicioOptional.isPresent());

        Servicio servicio = servicioOptional.get();
        servicio.setNombre(nuevoNombre);
        servicio.setPrecioHora(nuevoPrecio);

        Servicio servicioActualizado = servicioRepository.save(servicio);
        entityManager.flush();

        // Then
        assertEquals(nuevoNombre, servicioActualizado.getNombre());
        assertEquals(nuevoPrecio, servicioActualizado.getPrecioHora());

        // Verificar persistencia
        entityManager.clear();
        Optional<Servicio> servicioVerificacion =
                servicioRepository.findById(servicio1.getId());
        assertTrue(servicioVerificacion.isPresent());
        assertEquals(nuevoNombre, servicioVerificacion.get().getNombre());
        assertEquals(nuevoPrecio, servicioVerificacion.get().getPrecioHora());
    }

    @Test
    @DisplayName("Debería eliminar servicio correctamente")
    void deberiaEliminarServicio() {
        // Given
        Long servicioId = servicio1.getId();
        assertTrue(servicioRepository.existsById(servicioId));

        // When
        servicioRepository.deleteById(servicioId);
        entityManager.flush();

        // Then
        assertFalse(servicioRepository.existsById(servicioId));
        Optional<Servicio> servicioEliminado = servicioRepository.findById(servicioId);
        assertFalse(servicioEliminado.isPresent());
    }

    @Test
    @DisplayName("Debería encontrar todos los servicios")
    void deberiaEncontrarTodosLosServicios() {
        // When
        List<Servicio> todosLosServicios = servicioRepository.findAll();

        // Then
        assertNotNull(todosLosServicios);
        assertEquals(3, todosLosServicios.size());

        List<String> nombres = todosLosServicios.stream().map(Servicio::getNombre).toList();
        assertTrue(nombres.contains("Plomería"));
        assertTrue(nombres.contains("Electricidad"));
        assertTrue(nombres.contains("Carpintería"));
    }

    @Test
    @DisplayName("Debería contar servicios correctamente")
    void deberiaContarServicios() {
        // When
        long cantidadServicios = servicioRepository.count();

        // Then
        assertEquals(3, cantidadServicios);

        // Agregar un servicio más y verificar
        Servicio nuevoServicio = new Servicio();
        nuevoServicio.setNombre("Gas");
        nuevoServicio.setPrecioHora(28.00);
        entityManager.persistAndFlush(nuevoServicio);

        assertEquals(4, servicioRepository.count());
    }

    @Test
    @DisplayName("Debería validar restricciones de la entidad")
    void deberiaValidarRestricciones() {
        // Given - Crear servicio con nombre vacío
        Servicio servicioInvalido = new Servicio();
        servicioInvalido.setNombre(""); // Viola @NotBlank
        servicioInvalido.setPrecioHora(25.00);

        // When & Then
        assertThrows(Exception.class, () -> {
            entityManager.persistAndFlush(servicioInvalido);
        });
    }

    @Test
    @DisplayName("Debería validar restricción de precio null")
    void deberiaValidarRestriccionPrecioNull() {
        // Given - Crear servicio con precio null
        Servicio servicioInvalido = new Servicio();
        servicioInvalido.setNombre("Gas");
        servicioInvalido.setPrecioHora(null); // Viola @NotNull

        // When & Then
        assertThrows(Exception.class, () -> {
            entityManager.persistAndFlush(servicioInvalido);
        });
    }

    @Test
    @DisplayName("Debería respetar restricción de unicidad en el nombre")
    void deberiaRespetarRestriccionUnicidadNombre() {
        // Given - Intentar crear servicio con nombre duplicado
        Servicio servicioConNombreDuplicado = new Servicio();
        servicioConNombreDuplicado.setNombre("Plomería"); // Ya existe
        servicioConNombreDuplicado.setPrecioHora(40.00);

        // When & Then
        assertThrows(Exception.class, () -> {
            entityManager.persistAndFlush(servicioConNombreDuplicado);
        });
    }

    @Test
    @DisplayName("Debería encontrar servicio por ID específico")
    void deberiaEncontrarServicioPorIdEspecifico() {
        // When
        Optional<Servicio> servicioEncontrado = servicioRepository.findById(servicio2.getId());

        // Then
        assertTrue(servicioEncontrado.isPresent());
        assertEquals("Electricidad", servicioEncontrado.get().getNombre());
        assertEquals(30.00, servicioEncontrado.get().getPrecioHora());
    }

    @Test
    @DisplayName("Debería retornar Optional vacío para ID inexistente")
    void deberiaRetornarOptionalVacioParaIdInexistente() {
        // Given
        Long idInexistente = 999L;

        // When
        Optional<Servicio> servicioEncontrado = servicioRepository.findById(idInexistente);

        // Then
        assertFalse(servicioEncontrado.isPresent());
    }

    @Test
    @DisplayName("Debería verificar existencia de servicio por ID")
    void deberiaVerificarExistenciaServicioPorId() {
        // When & Then
        assertTrue(servicioRepository.existsById(servicio1.getId()));
        assertTrue(servicioRepository.existsById(servicio2.getId()));
        assertTrue(servicioRepository.existsById(servicio3.getId()));
        assertFalse(servicioRepository.existsById(999L));
    }

    @Test
    @DisplayName("Debería eliminar servicio por entidad")
    void deberiaEliminarServicioPorEntidad() {
        // Given
        Long servicioId = servicio3.getId();
        assertTrue(servicioRepository.existsById(servicioId));

        // When
        servicioRepository.delete(servicio3);
        entityManager.flush();

        // Then
        assertFalse(servicioRepository.existsById(servicioId));
    }

    @Test
    @DisplayName("Debería manejar servicios con precios decimales")
    void deberiaManejarServiciosConPreciosDecimales() {
        // Given
        Servicio servicioConDecimales = new Servicio();
        servicioConDecimales.setNombre("Albañilería");
        servicioConDecimales.setPrecioHora(22.75);

        // When
        Servicio servicioGuardado = servicioRepository.save(servicioConDecimales);
        entityManager.flush();
        entityManager.clear();

        Optional<Servicio> servicioRecuperado = 
                servicioRepository.findById(servicioGuardado.getId());

        // Then
        assertTrue(servicioRecuperado.isPresent());
        assertEquals(22.75, servicioRecuperado.get().getPrecioHora());
    }
}