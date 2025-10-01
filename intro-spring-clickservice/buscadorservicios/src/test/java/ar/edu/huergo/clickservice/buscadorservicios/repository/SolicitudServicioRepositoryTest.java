package ar.edu.huergo.clickservice.buscadorservicios.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
import ar.edu.huergo.clickservice.buscadorservicios.entity.SolicitudServicio;
import ar.edu.huergo.clickservice.buscadorservicios.entity.SolicitudServicio.EstadoSolicitud;
import ar.edu.huergo.clickservice.buscadorservicios.entity.security.Usuario;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

@DataJpaTest
@DisplayName("Tests de Validación en Repositorio - SolicitudServicio")
class SolicitudServicioRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SolicitudServicioRepository solicitudServicioRepository;

    @Autowired
    private Validator validator;

    private Servicio servicioGuardado;
    private Usuario clienteGuardado;

    @BeforeEach
    void setUp() {
        // Crear y guardar un Servicio mock
        servicioGuardado = new Servicio();
        servicioGuardado.setNombre("Plomería");
        servicioGuardado.setDescripcion("Servicios de plomería en general");
        servicioGuardado.setCategoria("Hogar");
        servicioGuardado = entityManager.persistAndFlush(servicioGuardado);

        // Crear y guardar un Usuario (Cliente) mock
        clienteGuardado = new Usuario();
        clienteGuardado.setUsername("cliente_test");
        clienteGuardado.setPassword("password123");
        clienteGuardado.setEmail("cliente@test.com");
        clienteGuardado.setNombreCompleto("Cliente Test");
        clienteGuardado = entityManager.persistAndFlush(clienteGuardado);

        entityManager.clear();
    }

    @Test
    @DisplayName("Debería guardar solicitud válida correctamente")
    void deberiaGuardarSolicitudValidaCorrectamente() {
        // Given
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioGuardado);
        solicitud.setCliente(clienteGuardado);
        solicitud.setDescripcionProblema("Tengo una fuga de agua en la cocina que necesita reparación urgente");
        solicitud.setDireccionServicio("Av. Corrientes 1500, CABA");
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(3));
        solicitud.setFranjaHoraria("Mañana");
        solicitud.setPresupuestoMaximo(5000.0);
        solicitud.setEstado(EstadoSolicitud.PENDIENTE);
        solicitud.setFechaCreacion(LocalDateTime.now());

        // When
        SolicitudServicio solicitudGuardada = solicitudServicioRepository.save(solicitud);
        entityManager.flush();

        // Then
        assertNotNull(solicitudGuardada.getId());
        assertEquals(solicitud.getDescripcionProblema(), solicitudGuardada.getDescripcionProblema());
        assertEquals(solicitud.getDireccionServicio(), solicitudGuardada.getDireccionServicio());
        assertEquals(solicitud.getPresupuestoMaximo(), solicitudGuardada.getPresupuestoMaximo());
        assertEquals(EstadoSolicitud.PENDIENTE, solicitudGuardada.getEstado());
    }

    @Test
    @DisplayName("Debería lanzar excepción al guardar solicitud sin servicio")
    void deberiaLanzarExcepcionAlGuardarSolicitudSinServicio() {
        // Given
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(null);
        solicitud.setCliente(clienteGuardado);
        solicitud.setDescripcionProblema("Reparación de instalación eléctrica");
        solicitud.setDireccionServicio("Calle Falsa 123");
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(5));
        solicitud.setFranjaHoraria("Tarde");
        solicitud.setPresupuestoMaximo(3000.0);

        // When & Then
        assertThrows(ConstraintViolationException.class, () -> {
            solicitudServicioRepository.save(solicitud);
            entityManager.flush();
        });
    }

    @Test
    @DisplayName("Debería lanzar excepción al guardar solicitud sin cliente")
    void deberiaLanzarExcepcionAlGuardarSolicitudSinCliente() {
        // Given
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioGuardado);
        solicitud.setCliente(null);
        solicitud.setDescripcionProblema("Instalación de aire acondicionado");
        solicitud.setDireccionServicio("Av. Santa Fe 2000");
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(10));
        solicitud.setFranjaHoraria("Mañana");
        solicitud.setPresupuestoMaximo(15000.0);

        // When & Then
        assertThrows(ConstraintViolationException.class, () -> {
            solicitudServicioRepository.save(solicitud);
            entityManager.flush();
        });
    }

    @Test
    @DisplayName("Debería lanzar excepción al guardar solicitud sin descripción problema")
    void deberiaLanzarExcepcionAlGuardarSolicitudSinDescripcionProblema() {
        // Given
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioGuardado);
        solicitud.setCliente(clienteGuardado);
        solicitud.setDescripcionProblema(null);
        solicitud.setDireccionServicio("Calle Principal 500");
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(4));
        solicitud.setFranjaHoraria("Tarde");
        solicitud.setPresupuestoMaximo(4000.0);

        // When & Then
        assertThrows(ConstraintViolationException.class, () -> {
            solicitudServicioRepository.save(solicitud);
            entityManager.flush();
        });
    }

    @Test
    @DisplayName("Debería lanzar excepción al guardar solicitud con descripción vacía")
    void deberiaLanzarExcepcionAlGuardarSolicitudConDescripcionVacia() {
        // Given
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioGuardado);
        solicitud.setCliente(clienteGuardado);
        solicitud.setDescripcionProblema("");
        solicitud.setDireccionServicio("Av. Belgrano 800");
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(2));
        solicitud.setFranjaHoraria("Noche");
        solicitud.setPresupuestoMaximo(2500.0);

        // When & Then
        assertThrows(ConstraintViolationException.class, () -> {
            solicitudServicioRepository.save(solicitud);
            entityManager.flush();
        });
    }

    @Test
    @DisplayName("Debería lanzar excepción al guardar solicitud con descripción muy corta")
    void deberiaLanzarExcepcionAlGuardarSolicitudConDescripcionMuyCorta() {
        // Given
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioGuardado);
        solicitud.setCliente(clienteGuardado);
        solicitud.setDescripcionProblema("Corta");
        solicitud.setDireccionServicio("Calle Test 100");
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(6));
        solicitud.setFranjaHoraria("Mañana");
        solicitud.setPresupuestoMaximo(3500.0);

        // When & Then
        assertThrows(ConstraintViolationException.class, () -> {
            solicitudServicioRepository.save(solicitud);
            entityManager.flush();
        });
    }

    @Test
    @DisplayName("Debería lanzar excepción al guardar solicitud con descripción muy larga")
    void deberiaLanzarExcepcionAlGuardarSolicitudConDescripcionMuyLarga() {
        // Given
        String descripcionLarga = "A".repeat(1001);
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioGuardado);
        solicitud.setCliente(clienteGuardado);
        solicitud.setDescripcionProblema(descripcionLarga);
        solicitud.setDireccionServicio("Av. Rivadavia 3000");
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(7));
        solicitud.setFranjaHoraria("Tarde");
        solicitud.setPresupuestoMaximo(6000.0);

        // When & Then
        assertThrows(ConstraintViolationException.class, () -> {
            solicitudServicioRepository.save(solicitud);
            entityManager.flush();
        });
    }

    @Test
    @DisplayName("Debería guardar solicitud con descripción en límite mínimo")
    void deberiaGuardarSolicitudConDescripcionEnLimiteMinimo() {
        // Given
        String descripcionMinima = "A".repeat(10);
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioGuardado);
        solicitud.setCliente(clienteGuardado);
        solicitud.setDescripcionProblema(descripcionMinima);
        solicitud.setDireccionServicio("Calle Mínima 10");
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(3));
        solicitud.setFranjaHoraria("Mañana");
        solicitud.setPresupuestoMaximo(2000.0);

        // When
        SolicitudServicio solicitudGuardada = solicitudServicioRepository.save(solicitud);
        entityManager.flush();

        // Then
        assertNotNull(solicitudGuardada.getId());
        assertEquals(10, solicitudGuardada.getDescripcionProblema().length());
    }

    @Test
    @DisplayName("Debería guardar solicitud con descripción en límite máximo")
    void deberiaGuardarSolicitudConDescripcionEnLimiteMaximo() {
        // Given
        String descripcionMaxima = "A".repeat(1000);
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioGuardado);
        solicitud.setCliente(clienteGuardado);
        solicitud.setDescripcionProblema(descripcionMaxima);
        solicitud.setDireccionServicio("Av. Máxima 1000");
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(8));
        solicitud.setFranjaHoraria("Tarde");
        solicitud.setPresupuestoMaximo(10000.0);

        // When
        SolicitudServicio solicitudGuardada = solicitudServicioRepository.save(solicitud);
        entityManager.flush();

        // Then
        assertNotNull(solicitudGuardada.getId());
        assertEquals(1000, solicitudGuardada.getDescripcionProblema().length());
    }

    @Test
    @DisplayName("Debería lanzar excepción al guardar solicitud sin dirección servicio")
    void deberiaLanzarExcepcionAlGuardarSolicitudSinDireccionServicio() {
        // Given
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioGuardado);
        solicitud.setCliente(clienteGuardado);
        solicitud.setDescripcionProblema("Reparación de cañerías en el baño");
        solicitud.setDireccionServicio(null);
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(5));
        solicitud.setFranjaHoraria("Mañana");
        solicitud.setPresupuestoMaximo(4500.0);

        // When & Then
        assertThrows(ConstraintViolationException.class, () -> {
            solicitudServicioRepository.save(solicitud);
            entityManager.flush();
        });
    }

    @Test
    @DisplayName("Debería lanzar excepción al guardar solicitud con dirección vacía")
    void deberiaLanzarExcepcionAlGuardarSolicitudConDireccionVacia() {
        // Given
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioGuardado);
        solicitud.setCliente(clienteGuardado);
        solicitud.setDescripcionProblema("Instalación de calefacción");
        solicitud.setDireccionServicio("");
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(9));
        solicitud.setFranjaHoraria("Tarde");
        solicitud.setPresupuestoMaximo(8000.0);

        // When & Then
        assertThrows(ConstraintViolationException.class, () -> {
            solicitudServicioRepository.save(solicitud);
            entityManager.flush();
        });
    }

    @Test
    @DisplayName("Debería lanzar excepción al guardar solicitud con dirección muy corta")
    void deberiaLanzarExcepcionAlGuardarSolicitudConDireccionMuyCorta() {
        // Given
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioGuardado);
        solicitud.setCliente(clienteGuardado);
        solicitud.setDescripcionProblema("Mantenimiento de jardín");
        solicitud.setDireccionServicio("Calle 1");
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(4));
        solicitud.setFranjaHoraria("Noche");
        solicitud.setPresupuestoMaximo(3000.0);

        // When & Then
        assertThrows(ConstraintViolationException.class, () -> {
            solicitudServicioRepository.save(solicitud);
            entityManager.flush();
        });
    }

    @Test
    @DisplayName("Debería lanzar excepción al guardar solicitud con dirección muy larga")
    void deberiaLanzarExcepcionAlGuardarSolicitudConDireccionMuyLarga() {
        // Given
        String direccionLarga = "A".repeat(201);
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioGuardado);
        solicitud.setCliente(clienteGuardado);
        solicitud.setDescripcionProblema("Servicio de limpieza profunda");
        solicitud.setDireccionServicio(direccionLarga);
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(6));
        solicitud.setFranjaHoraria("Mañana");
        solicitud.setPresupuestoMaximo(5500.0);

        // When & Then
        assertThrows(ConstraintViolationException.class, () -> {
            solicitudServicioRepository.save(solicitud);
            entityManager.flush();
        });
    }

    @Test
    @DisplayName("Debería guardar solicitud con dirección en límites válidos")
    void deberiaGuardarSolicitudConDireccionEnLimitesValidos() {
        // Given
        String direccionMinima = "A".repeat(10);
        String direccionMaxima = "A".repeat(200);

        SolicitudServicio solicitud1 = new SolicitudServicio();
        solicitud1.setServicio(servicioGuardado);
        solicitud1.setCliente(clienteGuardado);
        solicitud1.setDescripcionProblema("Reparación de ventanas");
        solicitud1.setDireccionServicio(direccionMinima);
        solicitud1.setFechaSolicitada(LocalDate.now().plusDays(3));
        solicitud1.setFranjaHoraria("Tarde");
        solicitud1.setPresupuestoMaximo(2500.0);

        SolicitudServicio solicitud2 = new SolicitudServicio();
        solicitud2.setServicio(servicioGuardado);
        solicitud2.setCliente(clienteGuardado);
        solicitud2.setDescripcionProblema("Cambio de cerraduras");
        solicitud2.setDireccionServicio(direccionMaxima);
        solicitud2.setFechaSolicitada(LocalDate.now().plusDays(5));
        solicitud2.setFranjaHoraria("Mañana");
        solicitud2.setPresupuestoMaximo(3500.0);

        // When
        SolicitudServicio guardada1 = solicitudServicioRepository.save(solicitud1);
        SolicitudServicio guardada2 = solicitudServicioRepository.save(solicitud2);
        entityManager.flush();

        // Then
        assertNotNull(guardada1.getId());
        assertNotNull(guardada2.getId());
        assertEquals(10, guardada1.getDireccionServicio().length());
        assertEquals(200, guardada2.getDireccionServicio().length());
    }

    @Test
    @DisplayName("Debería lanzar excepción al guardar solicitud sin fecha solicitada")
    void deberiaLanzarExcepcionAlGuardarSolicitudSinFechaSolicitada() {
        // Given
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioGuardado);
        solicitud.setCliente(clienteGuardado);
        solicitud.setDescripcionProblema("Instalación de cortinas");
        solicitud.setDireccionServicio("Av. Callao 1200");
        solicitud.setFechaSolicitada(null);
        solicitud.setFranjaHoraria("Tarde");
        solicitud.setPresupuestoMaximo(2000.0);

        // When & Then
        assertThrows(ConstraintViolationException.class, () -> {
            solicitudServicioRepository.save(solicitud);
            entityManager.flush();
        });
    }

    @Test
    @DisplayName("Debería lanzar excepción al guardar solicitud con fecha pasada")
    void deberiaLanzarExcepcionAlGuardarSolicitudConFechaPasada() {
        // Given
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioGuardado);
        solicitud.setCliente(clienteGuardado);
        solicitud.setDescripcionProblema("Servicio de fumigación");
        solicitud.setDireccionServicio("Calle Moreno 900");
        solicitud.setFechaSolicitada(LocalDate.now().minusDays(1));
        solicitud.setFranjaHoraria("Mañana");
        solicitud.setPresupuestoMaximo(4000.0);

        // When & Then
        assertThrows(ConstraintViolationException.class, () -> {
            solicitudServicioRepository.save(solicitud);
            entityManager.flush();
        });
    }

    @Test
    @DisplayName("Debería lanzar excepción al guardar solicitud con fecha actual")
    void deberiaLanzarExcepcionAlGuardarSolicitudConFechaActual() {
        // Given
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioGuardado);
        solicitud.setCliente(clienteGuardado);
        solicitud.setDescripcionProblema("Reparación urgente de techo");
        solicitud.setDireccionServicio("Av. Entre Ríos 600");
        solicitud.setFechaSolicitada(LocalDate.now());
        solicitud.setFranjaHoraria("Noche");
        solicitud.setPresupuestoMaximo(7000.0);

        // When & Then
        assertThrows(ConstraintViolationException.class, () -> {
            solicitudServicioRepository.save(solicitud);
            entityManager.flush();
        });
    }

    @Test
    @DisplayName("Debería guardar solicitud con fecha futura válida")
    void deberiaGuardarSolicitudConFechaFuturaValida() {
        // Given
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioGuardado);
        solicitud.setCliente(clienteGuardado);
        solicitud.setDescripcionProblema("Instalación de piso flotante");
        solicitud.setDireccionServicio("Calle Tucumán 1500");
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(15));
        solicitud.setFranjaHoraria("Mañana");
        solicitud.setPresupuestoMaximo(12000.0);

        // When
        SolicitudServicio solicitudGuardada = solicitudServicioRepository.save(solicitud);
        entityManager.flush();

        // Then
        assertNotNull(solicitudGuardada.getId());
        assertTrue(solicitudGuardada.getFechaSolicitada().isAfter(LocalDate.now()));
    }

    @Test
    @DisplayName("Debería lanzar excepción al guardar solicitud sin franja horaria")
    void deberiaLanzarExcepcionAlGuardarSolicitudSinFranjaHoraria() {
        // Given
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioGuardado);
        solicitud.setCliente(clienteGuardado);
        solicitud.setDescripcionProblema("Revisión de instalación de gas");
        solicitud.setDireccionServicio("Av. Pueyrredón 2000");
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(4));
        solicitud.setFranjaHoraria(null);
        solicitud.setPresupuestoMaximo(3500.0);

        // When & Then
        assertThrows(ConstraintViolationException.class, () -> {
            solicitudServicioRepository.save(solicitud);
            entityManager.flush();
        });
    }

    @Test
    @DisplayName("Debería lanzar excepción al guardar solicitud con franja horaria vacía")
    void deberiaLanzarExcepcionAlGuardarSolicitudConFranjaHorariaVacia() {
        // Given
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioGuardado);
        solicitud.setCliente(clienteGuardado);
        solicitud.setDescripcionProblema("Limpieza de tanque de agua");
        solicitud.setDireccionServicio("Calle Sarmiento 700");
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(7));
        solicitud.setFranjaHoraria("");
        solicitud.setPresupuestoMaximo(2800.0);

        // When & Then
        assertThrows(ConstraintViolationException.class, () -> {
            solicitudServicioRepository.save(solicitud);
            entityManager.flush();
        });
    }

    @Test
    @DisplayName("Debería lanzar excepción al guardar solicitud con franja horaria muy larga")
    void deberiaLanzarExcepcionAlGuardarSolicitudConFranjaHorariaMuyLarga() {
        // Given
        String franjaLarga = "A".repeat(51);
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioGuardado);
        solicitud.setCliente(clienteGuardado);
        solicitud.setDescripcionProblema("Instalación de sistema de riego");
        solicitud.setDireccionServicio("Av. Córdoba 3500");
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(10));
        solicitud.setFranjaHoraria(franjaLarga);
        solicitud.setPresupuestoMaximo(9000.0);

        // When & Then
        assertThrows(ConstraintViolationException.class, () -> {
            solicitudServicioRepository.save(solicitud);
            entityManager.flush();
        });
    }

    @Test
    @DisplayName("Debería guardar solicitud con franja horaria válida")
    void deberiaGuardarSolicitudConFranjaHorariaValida() {
        // Given
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioGuardado);
        solicitud.setCliente(clienteGuardado);
        solicitud.setDescripcionProblema("Construcción de deck en patio");
        solicitud.setDireccionServicio("Calle Perú 850");
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(20));
        solicitud.setFranjaHoraria("Todo el día");
        solicitud.setPresupuestoMaximo(18000.0);

        // When
        SolicitudServicio solicitudGuardada = solicitudServicioRepository.save(solicitud);
        entityManager.flush();

        // Then
        assertNotNull(solicitudGuardada.getId());
        assertEquals("Todo el día", solicitudGuardada.getFranjaHoraria());
    }

    @Test
    @DisplayName("Debería lanzar excepción al guardar solicitud sin presupuesto máximo")
    void deberiaLanzarExcepcionAlGuardarSolicitudSinPresupuestoMaximo() {
        // Given
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioGuardado);
        solicitud.setCliente(clienteGuardado);
        solicitud.setDescripcionProblema("Reparación de electrodomésticos");
        solicitud.setDireccionServicio("Av. Caseros 1800");
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(5));
        solicitud.setFranjaHoraria("Tarde");
        solicitud.setPresupuestoMaximo(null);

        // When & Then
        assertThrows(ConstraintViolationException.class, () -> {
            solicitudServicioRepository.save(solicitud);
            entityManager.flush();
        });
    }

    @Test
    @DisplayName("Debería lanzar excepción al guardar solicitud con presupuesto cero")
    void deberiaLanzarExcepcionAlGuardarSolicitudConPresupuestoCero() {
        // Given
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioGuardado);
        solicitud.setCliente(clienteGuardado);
        solicitud.setDescripcionProblema("Mantenimiento de aires acondicionados");
        solicitud.setDireccionServicio("Calle Alsina 950");
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(8));
        solicitud.setFranjaHoraria("Mañana");
        solicitud.setPresupuestoMaximo(0.0);

        // When & Then
        assertThrows(ConstraintViolationException.class, () -> {
            solicitudServicioRepository.save(solicitud);
            entityManager.flush();
        });
    }

    @Test
    @DisplayName("Debería lanzar excepción al guardar solicitud con presupuesto negativo")
    void deberiaLanzarExcepcionAlGuardarSolicitudConPresupuestoNegativo() {
        // Given
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioGuardado);
        solicitud.setCliente(clienteGuardado);
        solicitud.setDescripcionProblema("Pintura de fachada");
        solicitud.setDireccionServicio("Av. Jujuy 2200");
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(12));
        solicitud.setFranjaHoraria("Tarde");
        solicitud.setPresupuestoMaximo(-500.0);

        // When & Then
        assertThrows(ConstraintViolationException.class, () -> {
            solicitudServicioRepository.save(solicitud);
            entityManager.flush();
        });
    }

    @Test
    @DisplayName("Debería guardar solicitud con presupuesto positivo válido")
    void deberiaGuardarSolicitudConPresupuestoPositivoValido() {
        // Given
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioGuardado);
        solicitud.setCliente(clienteGuardado);
        solicitud.setDescripcionProblema("Instalación de sistema de seguridad");
        solicitud.setDireccionServicio("Av. Libertador 5000");
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(14));
        solicitud.setFranjaHoraria("Mañana");
        solicitud.setPresupuestoMaximo(25000.0);

        // When
        SolicitudServicio solicitudGuardada = solicitudServicioRepository.save(solicitud);
        entityManager.flush();

        // Then
        assertNotNull(solicitudGuardada.getId());
        assertEquals(25000.0, solicitudGuardada.getPresupuestoMaximo());
    }

    @Test
    @DisplayName("Debería lanzar excepción al guardar solicitud con comentarios muy largos")
    void deberiaLanzarExcepcionAlGuardarSolicitudConComentariosMuyLargos() {
        // Given
        String comentariosLargos = "A".repeat(501);
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioGuardado);
        solicitud.setCliente(clienteGuardado);
        solicitud.setDescripcionProblema("Servicio de jardinería completo");
        solicitud.setDireccionServicio("Calle Victoria 1200");
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(6));
        solicitud.setFranjaHoraria("Mañana");
        solicitud.setPresupuestoMaximo(7500.0);
        solicitud.setComentariosAdicionales(comentariosLargos);

        // When & Then
        assertThrows(ConstraintViolationException.class, () -> {
            solicitudServicioRepository.save(solicitud);
            entityManager.flush();
        });
    }

    @Test
    @DisplayName("Debería guardar solicitud con comentarios en límite válido")
    void deberiaGuardarSolicitudConComentariosEnLimiteValido() {
        // Given
        String comentariosMaximos = "A".repeat(500);
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioGuardado);
        solicitud.setCliente(clienteGuardado);
        solicitud.setDescripcionProblema("Remodelación integral de cocina");
        solicitud.setDireccionServicio("Av. Cabildo 3000");
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(25));
        solicitud.setFranjaHoraria("Todo el día");
        solicitud.setPresupuestoMaximo(50000.0);
        solicitud.setComentariosAdicionales(comentariosMaximos);

        // When
        SolicitudServicio solicitudGuardada = solicitudServicioRepository.save(solicitud);
        entityManager.flush();

        // Then
        assertNotNull(solicitudGuardada.getId());
        assertEquals(500, solicitudGuardada.getComentariosAdicionales().length());
    }

    @Test
    @DisplayName("Debería guardar solicitud sin comentarios adicionales")
    void deberiaGuardarSolicitudSinComentariosAdicionales() {
        // Given
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioGuardado);
        solicitud.setCliente(clienteGuardado);
        solicitud.setDescripcionProblema("Servicio de cerrajería de emergencia");
        solicitud.setDireccionServicio("Calle Balcarce 400");
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(2));
        solicitud.setFranjaHoraria("Noche");
        solicitud.setPresupuestoMaximo(1800.0);
        solicitud.setComentariosAdicionales(null);

        // When
        SolicitudServicio solicitudGuardada = solicitudServicioRepository.save(solicitud);
        entityManager.flush();

        // Then
        assertNotNull(solicitudGuardada.getId());
        assertEquals(null, solicitudGuardada.getComentariosAdicionales());
    }

    @Test
    @DisplayName("Debería establecer valores por defecto con @PrePersist")
    void deberiaEstablecerValoresPorDefectoConPrePersist() {
        // Given
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioGuardado);
        solicitud.setCliente(clienteGuardado);
        solicitud.setDescripcionProblema("Instalación de luminarias LED");
        solicitud.setDireccionServicio("Av. San Martín 1800");
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(9));
        solicitud.setFranjaHoraria("Tarde");
        solicitud.setPresupuestoMaximo(4200.0);
        // No establecer estado ni fechaCreacion

        // When
        SolicitudServicio solicitudGuardada = solicitudServicioRepository.save(solicitud);
        entityManager.flush();

        // Then
        assertNotNull(solicitudGuardada.getId());
        assertNotNull(solicitudGuardada.getFechaCreacion());
        assertEquals(EstadoSolicitud.PENDIENTE, solicitudGuardada.getEstado());
    }

    @Test
    @DisplayName("Debería guardar múltiples solicitudes del mismo cliente")
    void deberiaGuardarMultiplesSolicitudesDelMismoCliente() {
        // Given
        SolicitudServicio solicitud1 = new SolicitudServicio();
        solicitud1.setServicio(servicioGuardado);
        solicitud1.setCliente(clienteGuardado);
        solicitud1.setDescripcionProblema("Primera reparación de grifos");
        solicitud1.setDireccionServicio("Calle Primera 100");
        solicitud1.setFechaSolicitada(LocalDate.now().plusDays(3));
        solicitud1.setFranjaHoraria("Mañana");
        solicitud1.setPresupuestoMaximo(2000.0);

        SolicitudServicio solicitud2 = new SolicitudServicio();
        solicitud2.setServicio(servicioGuardado);
        solicitud2.setCliente(clienteGuardado);
        solicitud2.setDescripcionProblema("Segunda instalación de ducha");
        solicitud2.setDireccionServicio("Calle Segunda 200");
        solicitud2.setFechaSolicitada(LocalDate.now().plusDays(7));
        solicitud2.setFranjaHoraria("Tarde");
        solicitud2.setPresupuestoMaximo(5000.0);

        // When
        SolicitudServicio guardada1 = solicitudServicioRepository.save(solicitud1);
        SolicitudServicio guardada2 = solicitudServicioRepository.save(solicitud2);
        entityManager.flush();

        // Then
        assertNotNull(guardada1.getId());
        assertNotNull(guardada2.getId());
        assertEquals(clienteGuardado.getId(), guardada1.getCliente().getId());
        assertEquals(clienteGuardado.getId(), guardada2.getCliente().getId());
    }

    @Test
    @DisplayName("Debería guardar solicitud con todos los estados válidos")
    void deberiaGuardarSolicitudConTodosLosEstadosValidos() {
        // Given & When & Then
        for (EstadoSolicitud estado : EstadoSolicitud.values()) {
            SolicitudServicio solicitud = new SolicitudServicio();
            solicitud.setServicio(servicioGuardado);
            solicitud.setCliente(clienteGuardado);
            solicitud.setDescripcionProblema("Prueba estado: " + estado.getDescripcion());
            solicitud.setDireccionServicio("Calle Estado " + estado.ordinal());
            solicitud.setFechaSolicitada(LocalDate.now().plusDays(5));
            solicitud.setFranjaHoraria("Mañana");
            solicitud.setPresupuestoMaximo(3000.0);
            solicitud.setEstado(estado);

            SolicitudServicio guardada = solicitudServicioRepository.save(solicitud);
            entityManager.flush();
            entityManager.clear();

            assertNotNull(guardada.getId());
            assertEquals(estado, guardada.getEstado());
        }
    }

    @Test
    @DisplayName("Debería recuperar solicitud guardada por ID")
    void deberiaRecuperarSolicitudGuardadaPorId() {
        // Given
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioGuardado);
        solicitud.setCliente(clienteGuardado);
        solicitud.setDescripcionProblema("Reparación de caño maestro");
        solicitud.setDireccionServicio("Av. 9 de Julio 1000");
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(4));
        solicitud.setFranjaHoraria("Tarde");
        solicitud.setPresupuestoMaximo(6500.0);

        SolicitudServicio guardada = solicitudServicioRepository.save(solicitud);
        entityManager.flush();
        entityManager.clear();

        // When
        SolicitudServicio recuperada = solicitudServicioRepository.findById(guardada.getId()).orElse(null);

        // Then
        assertNotNull(recuperada);
        assertEquals(guardada.getId(), recuperada.getId());
        assertEquals("Reparación de caño maestro", recuperada.getDescripcionProblema());
        assertEquals("Av. 9 de Julio 1000", recuperada.getDireccionServicio());
        assertEquals(6500.0, recuperada.getPresupuestoMaximo());
    }

    @Test
    @DisplayName("Debería actualizar solicitud existente")
    void deberiaActualizarSolicitudExistente() {
        // Given
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioGuardado);
        solicitud.setCliente(clienteGuardado);
        solicitud.setDescripcionProblema("Instalación de lavamanos");
        solicitud.setDireccionServicio("Calle Defensa 700");
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(6));
        solicitud.setFranjaHoraria("Mañana");
        solicitud.setPresupuestoMaximo(3500.0);
        solicitud.setEstado(EstadoSolicitud.PENDIENTE);

        SolicitudServicio guardada = solicitudServicioRepository.save(solicitud);
        entityManager.flush();
        entityManager.clear();

        // When
        SolicitudServicio recuperada = solicitudServicioRepository.findById(guardada.getId()).orElse(null);
        recuperada.setEstado(EstadoSolicitud.ASIGNADA);
        recuperada.setComentariosAdicionales("Profesional asignado exitosamente");
        
        SolicitudServicio actualizada = solicitudServicioRepository.save(recuperada);
        entityManager.flush();

        // Then
        assertEquals(EstadoSolicitud.ASIGNADA, actualizada.getEstado());
        assertEquals("Profesional asignado exitosamente", actualizada.getComentariosAdicionales());
    }

    @Test
    @DisplayName("Debería eliminar solicitud existente")
    void deberiaEliminarSolicitudExistente() {
        // Given
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioGuardado);
        solicitud.setCliente(clienteGuardado);
        solicitud.setDescripcionProblema("Revisión de instalación sanitaria");
        solicitud.setDireccionServicio("Av. Independencia 2500");
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(8));
        solicitud.setFranjaHoraria("Tarde");
        solicitud.setPresupuestoMaximo(4800.0);

        SolicitudServicio guardada = solicitudServicioRepository.save(solicitud);
        entityManager.flush();
        Long id = guardada.getId();

        // When
        solicitudServicioRepository.deleteById(id);
        entityManager.flush();

        // Then
        assertFalse(solicitudServicioRepository.findById(id).isPresent());
    }

    @Test
    @DisplayName("Debería validar relación con servicio al guardar")
    void deberiaValidarRelacionConServicioAlGuardar() {
        // Given
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioGuardado);
        solicitud.setCliente(clienteGuardado);
        solicitud.setDescripcionProblema("Desobstrucción de desagües");
        solicitud.setDireccionServicio("Calle Piedras 1500");
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(3));
        solicitud.setFranjaHoraria("Noche");
        solicitud.setPresupuestoMaximo(2800.0);

        // When
        SolicitudServicio guardada = solicitudServicioRepository.save(solicitud);
        entityManager.flush();
        entityManager.clear();

        SolicitudServicio recuperada = solicitudServicioRepository.findById(guardada.getId()).orElse(null);

        // Then
        assertNotNull(recuperada.getServicio());
        assertEquals(servicioGuardado.getId(), recuperada.getServicio().getId());
        assertEquals("Plomería", recuperada.getServicio().getNombre());
    }

    @Test
    @DisplayName("Debería validar relación con cliente al guardar")
    void deberiaValidarRelacionConClienteAlGuardar() {
        // Given
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioGuardado);
        solicitud.setCliente(clienteGuardado);
        solicitud.setDescripcionProblema("Cambio de válvulas de paso");
        solicitud.setDireccionServicio("Av. Corrientes 2800");
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(5));
        solicitud.setFranjaHoraria("Mañana");
        solicitud.setPresupuestoMaximo(3200.0);

        // When
        SolicitudServicio guardada = solicitudServicioRepository.save(solicitud);
        entityManager.flush();
        entityManager.clear();

        SolicitudServicio recuperada = solicitudServicioRepository.findById(guardada.getId()).orElse(null);

        // Then
        assertNotNull(recuperada.getCliente());
        assertEquals(clienteGuardado.getId(), recuperada.getCliente().getId());
        assertEquals("cliente_test", recuperada.getCliente().getUsername());
    }

    @Test
    @DisplayName("Debería guardar solicitud con presupuesto decimal")
    void deberiaGuardarSolicitudConPresupuestoDecimal() {
        // Given
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioGuardado);
        solicitud.setCliente(clienteGuardado);
        solicitud.setDescripcionProblema("Instalación de bidet");
        solicitud.setDireccionServicio("Calle Chile 950");
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(7));
        solicitud.setFranjaHoraria("Tarde");
        solicitud.setPresupuestoMaximo(3599.99);

        // When
        SolicitudServicio guardada = solicitudServicioRepository.save(solicitud);
        entityManager.flush();

        // Then
        assertNotNull(guardada.getId());
        assertEquals(3599.99, guardada.getPresupuestoMaximo());
    }

    @Test
    @DisplayName("Debería guardar solicitud con descripción con caracteres especiales")
    void deberiaGuardarSolicitudConDescripcionConCaracteresEspeciales() {
        // Given
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioGuardado);
        solicitud.setCliente(clienteGuardado);
        solicitud.setDescripcionProblema("Reparación: 2 canillas + 1 inodoro (urgente!) ¿Cuánto sale?");
        solicitud.setDireccionServicio("Av. Rivadavia 5000");
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(2));
        solicitud.setFranjaHoraria("Mañana");
        solicitud.setPresupuestoMaximo(4000.0);

        // When
        SolicitudServicio guardada = solicitudServicioRepository.save(solicitud);
        entityManager.flush();

        // Then
        assertNotNull(guardada.getId());
        assertTrue(guardada.getDescripcionProblema().contains("¿"));
        assertTrue(guardada.getDescripcionProblema().contains("!"));
    }

    @Test
    @DisplayName("Debería guardar solicitud con dirección completa argentina")
    void deberiaGuardarSolicitudConDireccionCompletaArgentina() {
        // Given
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioGuardado);
        solicitud.setCliente(clienteGuardado);
        solicitud.setDescripcionProblema("Mantenimiento preventivo de cañerías");
        solicitud.setDireccionServicio("Av. Santa Fe 3456, Piso 10, Depto. B, C1425BGH, CABA, Argentina");
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(15));
        solicitud.setFranjaHoraria("Todo el día");
        solicitud.setPresupuestoMaximo(8500.0);

        // When
        SolicitudServicio guardada = solicitudServicioRepository.save(solicitud);
        entityManager.flush();

        // Then
        assertNotNull(guardada.getId());
        assertTrue(guardada.getDireccionServicio().contains("CABA"));
        assertTrue(guardada.getDireccionServicio().contains("Piso"));
    }

    @Test
    @DisplayName("Debería contar solicitudes guardadas")
    void deberiaContarSolicitudesGuardadas() {
        // Given
        long cantidadInicial = solicitudServicioRepository.count();

        SolicitudServicio solicitud1 = new SolicitudServicio();
        solicitud1.setServicio(servicioGuardado);
        solicitud1.setCliente(clienteGuardado);
        solicitud1.setDescripcionProblema("Primera solicitud de prueba");
        solicitud1.setDireccionServicio("Calle Test 100");
        solicitud1.setFechaSolicitada(LocalDate.now().plusDays(3));
        solicitud1.setFranjaHoraria("Mañana");
        solicitud1.setPresupuestoMaximo(2000.0);

        SolicitudServicio solicitud2 = new SolicitudServicio();
        solicitud2.setServicio(servicioGuardado);
        solicitud2.setCliente(clienteGuardado);
        solicitud2.setDescripcionProblema("Segunda solicitud de prueba");
        solicitud2.setDireccionServicio("Av. Test 200");
        solicitud2.setFechaSolicitada(LocalDate.now().plusDays(5));
        solicitud2.setFranjaHoraria("Tarde");
        solicitud2.setPresupuestoMaximo(3000.0);

        // When
        solicitudServicioRepository.save(solicitud1);
        solicitudServicioRepository.save(solicitud2);
        entityManager.flush();

        long cantidadFinal = solicitudServicioRepository.count();

        // Then
        assertEquals(cantidadInicial + 2, cantidadFinal);
    }

    @Test
    @DisplayName("Debería validar que se persiste fecha de creación automáticamente")
    void deberiaValidarQueSePersisteFechaCreacionAutomaticamente() {
        // Given
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioGuardado);
        solicitud.setCliente(clienteGuardado);
        solicitud.setDescripcionProblema("Instalación de termotanque");
        solicitud.setDireccionServicio("Calle México 1100");
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(10));
        solicitud.setFranjaHoraria("Mañana");
        solicitud.setPresupuestoMaximo(12000.0);
        // No establecer fechaCreacion manualmente

        LocalDateTime antes = LocalDateTime.now();

        // When
        SolicitudServicio guardada = solicitudServicioRepository.save(solicitud);
        entityManager.flush();

        LocalDateTime despues = LocalDateTime.now();

        // Then
        assertNotNull(guardada.getFechaCreacion());
        assertTrue(guardada.getFechaCreacion().isAfter(antes.minusSeconds(1)));
        assertTrue(guardada.getFechaCreacion().isBefore(despues.plusSeconds(1)));
    }

    @Test
    @DisplayName("Debería permitir actualizar fecha solicitada a otra fecha futura")
    void deberiaPermitirActualizarFechaSolicitadaAOtraFechaFutura() {
        // Given
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioGuardado);
        solicitud.setCliente(clienteGuardado);
        solicitud.setDescripcionProblema("Reparación programable de cañería");
        solicitud.setDireccionServicio("Av. Callao 2500");
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(5));
        solicitud.setFranjaHoraria("Tarde");
        solicitud.setPresupuestoMaximo(5500.0);

        SolicitudServicio guardada = solicitudServicioRepository.save(solicitud);
        entityManager.flush();
        entityManager.clear();

        // When
        SolicitudServicio recuperada = solicitudServicioRepository.findById(guardada.getId()).orElse(null);
        LocalDate nuevaFecha = LocalDate.now().plusDays(10);
        recuperada.setFechaSolicitada(nuevaFecha);
        
        SolicitudServicio actualizada = solicitudServicioRepository.save(recuperada);
        entityManager.flush();

        // Then
        assertEquals(nuevaFecha, actualizada.getFechaSolicitada());
    }
}