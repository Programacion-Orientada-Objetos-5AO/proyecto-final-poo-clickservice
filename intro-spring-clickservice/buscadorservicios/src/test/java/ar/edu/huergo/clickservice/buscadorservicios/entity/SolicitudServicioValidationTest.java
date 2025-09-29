package ar.edu.huergo.clickservice.buscadorservicios.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ar.edu.huergo.clickservice.buscadorservicios.entity.SolicitudServicio.EstadoSolicitud;
import ar.edu.huergo.clickservice.buscadorservicios.entity.security.Usuario;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@DisplayName("Tests de Validación - Entidad SolicitudServicio")
class SolicitudServicioValidationTest {

    private Validator validator;
    private Servicio servicioMock;
    private Usuario clienteMock;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        
        // Mock básico de Servicio
        servicioMock = new Servicio();
        servicioMock.setId(1L);
        
        // Mock básico de Usuario (Cliente)
        clienteMock = new Usuario();
        clienteMock.setId(1L);
    }

    @Test
    @DisplayName("Debería validar solicitud correcta sin errores")
    void deberiaValidarSolicitudCorrectaSinErrores() {
        // Given
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioMock);
        solicitud.setCliente(clienteMock);
        solicitud.setDescripcionProblema("Necesito reparar una gotera en el baño que está causando humedad");
        solicitud.setDireccionServicio("Av. Corrientes 1234, CABA");
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(5));
        solicitud.setFranjaHoraria("Mañana");
        solicitud.setPresupuestoMaximo(5000.0);
        solicitud.setEstado(EstadoSolicitud.PENDIENTE);

        // When
        Set<ConstraintViolation<SolicitudServicio>> violaciones = validator.validate(solicitud);

        // Then
        assertTrue(violaciones.isEmpty(),
                "No debería haber violaciones de validación para una solicitud válida");
    }

    @Test
    @DisplayName("Debería fallar validación con servicio null")
    void deberiaFallarValidacionConServicioNull() {
        // Given
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(null);
        solicitud.setCliente(clienteMock);
        solicitud.setDescripcionProblema("Reparación de instalación eléctrica");
        solicitud.setDireccionServicio("Calle Falsa 123");
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(3));
        solicitud.setFranjaHoraria("Tarde");
        solicitud.setPresupuestoMaximo(3000.0);

        // When
        Set<ConstraintViolation<SolicitudServicio>> violaciones = validator.validate(solicitud);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("servicio")));
        assertTrue(violaciones.stream().anyMatch(v -> v.getMessage().contains("obligatorio")));
    }

    @Test
    @DisplayName("Debería fallar validación con cliente null")
    void deberiaFallarValidacionConClienteNull() {
        // Given
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioMock);
        solicitud.setCliente(null);
        solicitud.setDescripcionProblema("Instalación de aire acondicionado");
        solicitud.setDireccionServicio("Av. Santa Fe 5678");
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(7));
        solicitud.setFranjaHoraria("Mañana");
        solicitud.setPresupuestoMaximo(10000.0);

        // When
        Set<ConstraintViolation<SolicitudServicio>> violaciones = validator.validate(solicitud);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("cliente")));
        assertTrue(violaciones.stream().anyMatch(v -> v.getMessage().contains("obligatorio")));
    }

    @Test
    @DisplayName("Debería fallar validación con descripción problema null")
    void deberiaFallarValidacionConDescripcionProblemaNull() {
        // Given
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioMock);
        solicitud.setCliente(clienteMock);
        solicitud.setDescripcionProblema(null);
        solicitud.setDireccionServicio("Calle Principal 999");
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(2));
        solicitud.setFranjaHoraria("Noche");
        solicitud.setPresupuestoMaximo(2000.0);

        // When
        Set<ConstraintViolation<SolicitudServicio>> violaciones = validator.validate(solicitud);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("descripcionProblema")));
        assertTrue(violaciones.stream().anyMatch(v -> v.getMessage().contains("obligatoria")));
    }

    @Test
    @DisplayName("Debería fallar validación con descripción problema vacía")
    void deberiaFallarValidacionConDescripcionProblemaVacia() {
        // Given
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioMock);
        solicitud.setCliente(clienteMock);
        solicitud.setDescripcionProblema("");
        solicitud.setDireccionServicio("Calle Mayor 100");
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(4));
        solicitud.setFranjaHoraria("Tarde");
        solicitud.setPresupuestoMaximo(1500.0);

        // When
        Set<ConstraintViolation<SolicitudServicio>> violaciones = validator.validate(solicitud);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("descripcionProblema")));
    }

    @Test
    @DisplayName("Debería fallar validación con descripción problema solo espacios")
    void deberiaFallarValidacionConDescripcionProblemaSoloEspacios() {
        // Given
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioMock);
        solicitud.setCliente(clienteMock);
        solicitud.setDescripcionProblema("   ");
        solicitud.setDireccionServicio("Av. Libertador 2000");
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(1));
        solicitud.setFranjaHoraria("Mañana");
        solicitud.setPresupuestoMaximo(4000.0);

        // When
        Set<ConstraintViolation<SolicitudServicio>> violaciones = validator.validate(solicitud);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("descripcionProblema")));
    }

    @Test
    @DisplayName("Debería fallar validación con descripción problema muy corta")
    void deberiaFallarValidacionConDescripcionProblemaMuyCorta() {
        // Given
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioMock);
        solicitud.setCliente(clienteMock);
        solicitud.setDescripcionProblema("Corta");
        solicitud.setDireccionServicio("Calle Test 123");
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(5));
        solicitud.setFranjaHoraria("Tarde");
        solicitud.setPresupuestoMaximo(2500.0);

        // When
        Set<ConstraintViolation<SolicitudServicio>> violaciones = validator.validate(solicitud);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("descripcionProblema")));
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getMessage().contains("entre 10 y 1000 caracteres")));
    }

    @Test
    @DisplayName("Debería fallar validación con descripción problema muy larga")
    void deberiaFallarValidacionConDescripcionProblemaMuyLarga() {
        // Given
        String descripcionLarga = "A".repeat(1001);
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioMock);
        solicitud.setCliente(clienteMock);
        solicitud.setDescripcionProblema(descripcionLarga);
        solicitud.setDireccionServicio("Av. Principal 500");
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(3));
        solicitud.setFranjaHoraria("Mañana");
        solicitud.setPresupuestoMaximo(8000.0);

        // When
        Set<ConstraintViolation<SolicitudServicio>> violaciones = validator.validate(solicitud);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("descripcionProblema")));
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getMessage().contains("entre 10 y 1000 caracteres")));
    }

    @Test
    @DisplayName("Debería aceptar descripción problema en el límite válido")
    void deberiaAceptarDescripcionProblemaEnLimiteValido() {
        // Given
        String descripcionMinima = "A".repeat(10);
        String descripcionMaxima = "A".repeat(1000);

        SolicitudServicio solicitud1 = new SolicitudServicio();
        solicitud1.setServicio(servicioMock);
        solicitud1.setCliente(clienteMock);
        solicitud1.setDescripcionProblema(descripcionMinima);
        solicitud1.setDireccionServicio("Calle Test 100");
        solicitud1.setFechaSolicitada(LocalDate.now().plusDays(2));
        solicitud1.setFranjaHoraria("Tarde");
        solicitud1.setPresupuestoMaximo(3000.0);

        SolicitudServicio solicitud2 = new SolicitudServicio();
        solicitud2.setServicio(servicioMock);
        solicitud2.setCliente(clienteMock);
        solicitud2.setDescripcionProblema(descripcionMaxima);
        solicitud2.setDireccionServicio("Av. Test 200");
        solicitud2.setFechaSolicitada(LocalDate.now().plusDays(3));
        solicitud2.setFranjaHoraria("Mañana");
        solicitud2.setPresupuestoMaximo(5000.0);

        // When
        Set<ConstraintViolation<SolicitudServicio>> violaciones1 = validator.validate(solicitud1);
        Set<ConstraintViolation<SolicitudServicio>> violaciones2 = validator.validate(solicitud2);

        // Then
        assertTrue(violaciones1.isEmpty());
        assertTrue(violaciones2.isEmpty());
    }

    @Test
    @DisplayName("Debería fallar validación con dirección servicio null")
    void deberiaFallarValidacionConDireccionServicioNull() {
        // Given
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioMock);
        solicitud.setCliente(clienteMock);
        solicitud.setDescripcionProblema("Reparación urgente de cañerías");
        solicitud.setDireccionServicio(null);
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(1));
        solicitud.setFranjaHoraria("Mañana");
        solicitud.setPresupuestoMaximo(6000.0);

        // When
        Set<ConstraintViolation<SolicitudServicio>> violaciones = validator.validate(solicitud);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("direccionServicio")));
        assertTrue(violaciones.stream().anyMatch(v -> v.getMessage().contains("obligatoria")));
    }

    @Test
    @DisplayName("Debería fallar validación con dirección servicio vacía")
    void deberiaFallarValidacionConDireccionServicioVacia() {
        // Given
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioMock);
        solicitud.setCliente(clienteMock);
        solicitud.setDescripcionProblema("Pintura de paredes interiores");
        solicitud.setDireccionServicio("");
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(10));
        solicitud.setFranjaHoraria("Tarde");
        solicitud.setPresupuestoMaximo(7000.0);

        // When
        Set<ConstraintViolation<SolicitudServicio>> violaciones = validator.validate(solicitud);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("direccionServicio")));
    }

    @Test
    @DisplayName("Debería fallar validación con dirección servicio muy corta")
    void deberiaFallarValidacionConDireccionServicioMuyCorta() {
        // Given
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioMock);
        solicitud.setCliente(clienteMock);
        solicitud.setDescripcionProblema("Instalación de calefacción");
        solicitud.setDireccionServicio("Calle 1");
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(6));
        solicitud.setFranjaHoraria("Noche");
        solicitud.setPresupuestoMaximo(9000.0);

        // When
        Set<ConstraintViolation<SolicitudServicio>> violaciones = validator.validate(solicitud);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("direccionServicio")));
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getMessage().contains("entre 10 y 200 caracteres")));
    }

    @Test
    @DisplayName("Debería fallar validación con dirección servicio muy larga")
    void deberiaFallarValidacionConDireccionServicioMuyLarga() {
        // Given
        String direccionLarga = "A".repeat(201);
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioMock);
        solicitud.setCliente(clienteMock);
        solicitud.setDescripcionProblema("Revisión de sistema de gas");
        solicitud.setDireccionServicio(direccionLarga);
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(4));
        solicitud.setFranjaHoraria("Mañana");
        solicitud.setPresupuestoMaximo(4500.0);

        // When
        Set<ConstraintViolation<SolicitudServicio>> violaciones = validator.validate(solicitud);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("direccionServicio")));
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getMessage().contains("entre 10 y 200 caracteres")));
    }

    @Test
    @DisplayName("Debería aceptar dirección servicio en el límite válido")
    void deberiaAceptarDireccionServicioEnLimiteValido() {
        // Given
        String direccionMinima = "A".repeat(10);
        String direccionMaxima = "A".repeat(200);

        SolicitudServicio solicitud1 = new SolicitudServicio();
        solicitud1.setServicio(servicioMock);
        solicitud1.setCliente(clienteMock);
        solicitud1.setDescripcionProblema("Servicio de limpieza profunda");
        solicitud1.setDireccionServicio(direccionMinima);
        solicitud1.setFechaSolicitada(LocalDate.now().plusDays(5));
        solicitud1.setFranjaHoraria("Tarde");
        solicitud1.setPresupuestoMaximo(3500.0);

        SolicitudServicio solicitud2 = new SolicitudServicio();
        solicitud2.setServicio(servicioMock);
        solicitud2.setCliente(clienteMock);
        solicitud2.setDescripcionProblema("Instalación de sistema de seguridad");
        solicitud2.setDireccionServicio(direccionMaxima);
        solicitud2.setFechaSolicitada(LocalDate.now().plusDays(8));
        solicitud2.setFranjaHoraria("Mañana");
        solicitud2.setPresupuestoMaximo(12000.0);

        // When
        Set<ConstraintViolation<SolicitudServicio>> violaciones1 = validator.validate(solicitud1);
        Set<ConstraintViolation<SolicitudServicio>> violaciones2 = validator.validate(solicitud2);

        // Then
        assertTrue(violaciones1.isEmpty());
        assertTrue(violaciones2.isEmpty());
    }

    @Test
    @DisplayName("Debería fallar validación con fecha solicitada null")
    void deberiaFallarValidacionConFechaSolicitadaNull() {
        // Given
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioMock);
        solicitud.setCliente(clienteMock);
        solicitud.setDescripcionProblema("Reparación de electrodomésticos");
        solicitud.setDireccionServicio("Av. Rivadavia 3000");
        solicitud.setFechaSolicitada(null);
        solicitud.setFranjaHoraria("Tarde");
        solicitud.setPresupuestoMaximo(2000.0);

        // When
        Set<ConstraintViolation<SolicitudServicio>> violaciones = validator.validate(solicitud);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("fechaSolicitada")));
        assertTrue(violaciones.stream().anyMatch(v -> v.getMessage().contains("obligatoria")));
    }

    @Test
    @DisplayName("Debería fallar validación con fecha solicitada pasada")
    void deberiaFallarValidacionConFechaSolicitadaPasada() {
        // Given
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioMock);
        solicitud.setCliente(clienteMock);
        solicitud.setDescripcionProblema("Mantenimiento de jardín");
        solicitud.setDireccionServicio("Calle Belgrano 1500");
        solicitud.setFechaSolicitada(LocalDate.now().minusDays(1));
        solicitud.setFranjaHoraria("Mañana");
        solicitud.setPresupuestoMaximo(3000.0);

        // When
        Set<ConstraintViolation<SolicitudServicio>> violaciones = validator.validate(solicitud);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("fechaSolicitada")));
        assertTrue(violaciones.stream().anyMatch(v -> v.getMessage().contains("futura")));
    }

    @Test
    @DisplayName("Debería fallar validación con fecha solicitada actual")
    void deberiaFallarValidacionConFechaSolicitadaActual() {
        // Given
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioMock);
        solicitud.setCliente(clienteMock);
        solicitud.setDescripcionProblema("Servicio de fumigación");
        solicitud.setDireccionServicio("Av. San Martín 800");
        solicitud.setFechaSolicitada(LocalDate.now());
        solicitud.setFranjaHoraria("Noche");
        solicitud.setPresupuestoMaximo(5000.0);

        // When
        Set<ConstraintViolation<SolicitudServicio>> violaciones = validator.validate(solicitud);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("fechaSolicitada")));
        assertTrue(violaciones.stream().anyMatch(v -> v.getMessage().contains("futura")));
    }

    @Test
    @DisplayName("Debería aceptar fecha solicitada futura")
    void deberiaAceptarFechaSolicitadaFutura() {
        // Given
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioMock);
        solicitud.setCliente(clienteMock);
        solicitud.setDescripcionProblema("Instalación de piso flotante");
        solicitud.setDireccionServicio("Calle Mitre 2200");
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(15));
        solicitud.setFranjaHoraria("Mañana");
        solicitud.setPresupuestoMaximo(15000.0);

        // When
        Set<ConstraintViolation<SolicitudServicio>> violaciones = validator.validate(solicitud);

        // Then
        assertTrue(violaciones.isEmpty());
    }

    @Test
    @DisplayName("Debería fallar validación con franja horaria null")
    void deberiaFallarValidacionConFranjaHorariaNull() {
        // Given
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioMock);
        solicitud.setCliente(clienteMock);
        solicitud.setDescripcionProblema("Reparación de ventanas");
        solicitud.setDireccionServicio("Av. Pueyrredón 1000");
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(3));
        solicitud.setFranjaHoraria(null);
        solicitud.setPresupuestoMaximo(4000.0);

        // When
        Set<ConstraintViolation<SolicitudServicio>> violaciones = validator.validate(solicitud);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("franjaHoraria")));
        assertTrue(violaciones.stream().anyMatch(v -> v.getMessage().contains("obligatoria")));
    }

    @Test
    @DisplayName("Debería fallar validación con franja horaria vacía")
    void deberiaFallarValidacionConFranjaHorariaVacia() {
        // Given
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioMock);
        solicitud.setCliente(clienteMock);
        solicitud.setDescripcionProblema("Cambio de cerraduras");
        solicitud.setDireccionServicio("Calle Sarmiento 700");
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(2));
        solicitud.setFranjaHoraria("");
        solicitud.setPresupuestoMaximo(2500.0);

        // When
        Set<ConstraintViolation<SolicitudServicio>> violaciones = validator.validate(solicitud);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("franjaHoraria")));
    }

    @Test
    @DisplayName("Debería fallar validación con franja horaria muy larga")
    void deberiaFallarValidacionConFranjaHorariaMuyLarga() {
        // Given
        String franjaLarga = "A".repeat(51);
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioMock);
        solicitud.setCliente(clienteMock);
        solicitud.setDescripcionProblema("Instalación de cortinas");
        solicitud.setDireccionServicio("Av. Callao 1200");
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(4));
        solicitud.setFranjaHoraria(franjaLarga);
        solicitud.setPresupuestoMaximo(3500.0);

        // When
        Set<ConstraintViolation<SolicitudServicio>> violaciones = validator.validate(solicitud);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("franjaHoraria")));
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getMessage().contains("no puede exceder los 50 caracteres")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Mañana", "Tarde", "Noche", "Todo el día", "08:00 - 12:00"})
    @DisplayName("Debería aceptar franjas horarias válidas")
    void deberiaAceptarFranjasHorariasValidas(String franjaValida) {
        // Given
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioMock);
        solicitud.setCliente(clienteMock);
        solicitud.setDescripcionProblema("Servicio de tapicería");
        solicitud.setDireccionServicio("Calle Córdoba 900");
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(7));
        solicitud.setFranjaHoraria(franjaValida);
        solicitud.setPresupuestoMaximo(6000.0);

        // When
        Set<ConstraintViolation<SolicitudServicio>> violaciones = validator.validate(solicitud);

        // Then
        assertTrue(violaciones.isEmpty(), "La franja horaria '" + franjaValida + "' debería ser válida");
    }

    @Test
    @DisplayName("Debería fallar validación con presupuesto máximo null")
    void deberiaFallarValidacionConPresupuestoMaximoNull() {
        // Given
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioMock);
        solicitud.setCliente(clienteMock);
        solicitud.setDescripcionProblema("Reparación de techo");
        solicitud.setDireccionServicio("Av. Entre Ríos 600");
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(10));
        solicitud.setFranjaHoraria("Mañana");
        solicitud.setPresupuestoMaximo(null);

        // When
        Set<ConstraintViolation<SolicitudServicio>> violaciones = validator.validate(solicitud);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("presupuestoMaximo")));
        assertTrue(violaciones.stream().anyMatch(v -> v.getMessage().contains("obligatorio")));
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, -100.0, -0.5, -1000.0})
    @DisplayName("Debería fallar validación con presupuesto máximo cero o negativo")
    void deberiaFallarValidacionConPresupuestoMaximoCeroONegativo(double presupuestoInvalido) {
        // Given
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioMock);
        solicitud.setCliente(clienteMock);
        solicitud.setDescripcionProblema("Limpieza de tanque de agua");
        solicitud.setDireccionServicio("Calle Moreno 1100");
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(5));
        solicitud.setFranjaHoraria("Tarde");
        solicitud.setPresupuestoMaximo(presupuestoInvalido);

        // When
        Set<ConstraintViolation<SolicitudServicio>> violaciones = validator.validate(solicitud);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("presupuestoMaximo")));
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getMessage().contains("mayor a 0")));
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.01, 100.0, 1000.0, 50000.0, 999999.99})
    @DisplayName("Debería aceptar presupuestos máximos válidos")
    void deberiaAceptarPresupuestosMaximosValidos(double presupuestoValido) {
        // Given
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioMock);
        solicitud.setCliente(clienteMock);
        solicitud.setDescripcionProblema("Remodelación completa de baño");
        solicitud.setDireccionServicio("Av. Independencia 3500");
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(20));
        solicitud.setFranjaHoraria("Mañana");
        solicitud.setPresupuestoMaximo(presupuestoValido);

        // When
        Set<ConstraintViolation<SolicitudServicio>> violaciones = validator.validate(solicitud);

        // Then
        assertTrue(violaciones.isEmpty(), "El presupuesto " + presupuestoValido + " debería ser válido");
    }

    @Test
    @DisplayName("Debería fallar validación con comentarios adicionales muy largos")
    void deberiaFallarValidacionConComentariosAdicionalesMuyLargos() {
        // Given
        String comentariosLargos = "A".repeat(501);
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioMock);
        solicitud.setCliente(clienteMock);
        solicitud.setDescripcionProblema("Instalación de sistema de riego");
        solicitud.setDireccionServicio("Calle Jujuy 1800");
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(12));
        solicitud.setFranjaHoraria("Tarde");
        solicitud.setPresupuestoMaximo(8000.0);
        solicitud.setComentariosAdicionales(comentariosLargos);

        // When
        Set<ConstraintViolation<SolicitudServicio>> violaciones = validator.validate(solicitud);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("comentariosAdicionales")));
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getMessage().contains("no pueden exceder los 500 caracteres")));
    }

    @Test
    @DisplayName("Debería aceptar comentarios adicionales en el límite válido")
    void deberiaAceptarComentariosAdicionalesEnLimiteValido() {
        // Given
        String comentariosMaximos = "A".repeat(500);
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioMock);
        solicitud.setCliente(clienteMock);
        solicitud.setDescripcionProblema("Construcción de deck en patio");
        solicitud.setDireccionServicio("Av. Caseros 2500");
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(30));
        solicitud.setFranjaHoraria("Todo el día");
        solicitud.setPresupuestoMaximo(25000.0);
        solicitud.setComentariosAdicionales(comentariosMaximos);

        // When
        Set<ConstraintViolation<SolicitudServicio>> violaciones = validator.validate(solicitud);

        // Then
        assertTrue(violaciones.isEmpty());
    }

    @Test
    @DisplayName("Debería aceptar solicitud sin comentarios adicionales")
    void deberiaAceptarSolicitudSinComentariosAdicionales() {
        // Given
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioMock);
        solicitud.setCliente(clienteMock);
        solicitud.setDescripcionProblema("Servicio de cerrajería urgente");
        solicitud.setDireccionServicio("Calle Alsina 950");
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(1));
        solicitud.setFranjaHoraria("Noche");
        solicitud.setPresupuestoMaximo(1500.0);
        solicitud.setComentariosAdicionales(null);

        // When
        Set<ConstraintViolation<SolicitudServicio>> violaciones = validator.validate(solicitud);

        // Then
        assertTrue(violaciones.isEmpty());
    }

    @Test
    @DisplayName("Debería validar múltiples errores simultáneamente")
    void deberiaValidarMultiplesErroresSimultaneamente() {
        // Given - Solicitud con múltiples errores
        SolicitudServicio solicitudInvalida = new SolicitudServicio();
        solicitudInvalida.setServicio(null);
        solicitudInvalida.setCliente(null);
        solicitudInvalida.setDescripcionProblema("");
        solicitudInvalida.setDireccionServicio("Corta");
        solicitudInvalida.setFechaSolicitada(LocalDate.now().minusDays(1));
        solicitudInvalida.setFranjaHoraria("");
        solicitudInvalida.setPresupuestoMaximo(-500.0);

        // When
        Set<ConstraintViolation<SolicitudServicio>> violaciones = validator.validate(solicitudInvalida);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.size() >= 7);

        List<String> propiedadesConError =
                violaciones.stream().map(v -> v.getPropertyPath().toString()).toList();

        assertTrue(propiedadesConError.contains("servicio"));
        assertTrue(propiedadesConError.contains("cliente"));
        assertTrue(propiedadesConError.contains("descripcionProblema"));
        assertTrue(propiedadesConError.contains("direccionServicio"));
        assertTrue(propiedadesConError.contains("fechaSolicitada"));
        assertTrue(propiedadesConError.contains("franjaHoraria"));
        assertTrue(propiedadesConError.contains("presupuestoMaximo"));
    }

    @Test
    @DisplayName("Debería validar solicitud con valores por defecto")
    void deberiaValidarSolicitudConValoresPorDefecto() {
        // Given
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioMock);
        solicitud.setCliente(clienteMock);
        solicitud.setDescripcionProblema("Mantenimiento preventivo de instalaciones");
        solicitud.setDireccionServicio("Av. Corrientes 5000");
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(14));
        solicitud.setFranjaHoraria("Mañana");
        solicitud.setPresupuestoMaximo(10000.0);
        solicitud.onCreate();

        // When
        Set<ConstraintViolation<SolicitudServicio>> violaciones = validator.validate(solicitud);

        // Then
        assertTrue(violaciones.isEmpty());
        assertTrue(solicitud.getFechaCreacion() != null);
        assertTrue(solicitud.getEstado() == EstadoSolicitud.PENDIENTE);
    }

    @Test
    @DisplayName("Debería validar solicitud completa con constructor")
    void deberiaValidarSolicitudCompletaConConstructor() {
        // Given
        SolicitudServicio solicitud = new SolicitudServicio(
                1L,
                servicioMock,
                clienteMock,
                "Necesito reparar una filtración de agua en el techo del baño principal",
                "Av. Santa Fe 1234, Piso 5, Depto B, CABA",
                LocalDate.now().plusDays(7),
                "Mañana (9:00 - 13:00)",
                8500.0,
                EstadoSolicitud.PENDIENTE,
                LocalDateTime.now(),
                "Preferiblemente que traiga los materiales necesarios"
        );

        // When
        Set<ConstraintViolation<SolicitudServicio>> violaciones = validator.validate(solicitud);

        // Then
        assertTrue(violaciones.isEmpty());
    }

    @Test
    @DisplayName("Debería aceptar descripción problema con caracteres especiales")
    void deberiaAceptarDescripcionProblemaConCaracteresEspeciales() {
        // Given
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioMock);
        solicitud.setCliente(clienteMock);
        solicitud.setDescripcionProblema("Reparación de instalación eléctrica: 3 tomacorrientes + lámpara (220v)");
        solicitud.setDireccionServicio("Calle Perú 850, CABA");
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(5));
        solicitud.setFranjaHoraria("Tarde");
        solicitud.setPresupuestoMaximo(4500.0);

        // When
        Set<ConstraintViolation<SolicitudServicio>> violaciones = validator.validate(solicitud);

        // Then
        assertTrue(violaciones.isEmpty());
    }

    @Test
    @DisplayName("Debería aceptar dirección con formato completo argentino")
    void deberiaAceptarDireccionConFormatoCompletoArgentino() {
        // Given
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioMock);
        solicitud.setCliente(clienteMock);
        solicitud.setDescripcionProblema("Instalación de aire acondicionado split");
        solicitud.setDireccionServicio("Av. Rivadavia 3456, Piso 7, Depto. C, C1203AAB, CABA");
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(9));
        solicitud.setFranjaHoraria("Mañana");
        solicitud.setPresupuestoMaximo(15000.0);

        // When
        Set<ConstraintViolation<SolicitudServicio>> violaciones = validator.validate(solicitud);

        // Then
        assertTrue(violaciones.isEmpty());
    }

    @Test
    @DisplayName("Debería validar todos los estados de solicitud")
    void deberiaValidarTodosLosEstadosSolicitud() {
        // Given
        EstadoSolicitud[] estados = EstadoSolicitud.values();

        for (EstadoSolicitud estado : estados) {
            SolicitudServicio solicitud = new SolicitudServicio();
            solicitud.setServicio(servicioMock);
            solicitud.setCliente(clienteMock);
            solicitud.setDescripcionProblema("Prueba de estado: " + estado.getDescripcion());
            solicitud.setDireccionServicio("Calle Estado 100");
            solicitud.setFechaSolicitada(LocalDate.now().plusDays(3));
            solicitud.setFranjaHoraria("Tarde");
            solicitud.setPresupuestoMaximo(5000.0);
            solicitud.setEstado(estado);

            // When
            Set<ConstraintViolation<SolicitudServicio>> violaciones = validator.validate(solicitud);

            // Then
            assertTrue(violaciones.isEmpty(), "El estado " + estado + " debería ser válido");
        }
    }

    @Test
    @DisplayName("Debería aceptar fecha solicitada lejana en el futuro")
    void deberiaAceptarFechaSolicitadaLejanaEnElFuturo() {
        // Given
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioMock);
        solicitud.setCliente(clienteMock);
        solicitud.setDescripcionProblema("Proyecto de remodelación integral programado");
        solicitud.setDireccionServicio("Av. Del Libertador 8000");
        solicitud.setFechaSolicitada(LocalDate.now().plusMonths(3));
        solicitud.setFranjaHoraria("Todo el día");
        solicitud.setPresupuestoMaximo(100000.0);

        // When
        Set<ConstraintViolation<SolicitudServicio>> violaciones = validator.validate(solicitud);

        // Then
        assertTrue(violaciones.isEmpty());
    }

    @Test
    @DisplayName("Debería manejar descripción con saltos de línea y espacios")
    void deberiaManejarDescripcionConSaltosDeLineaYEspacios() {
        // Given
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioMock);
        solicitud.setCliente(clienteMock);
        solicitud.setDescripcionProblema("Necesito:\n1. Reparar grifo\n2. Cambiar desagüe\n3. Revisar cañerías");
        solicitud.setDireccionServicio("Calle Victoria 1200");
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(6));
        solicitud.setFranjaHoraria("Mañana");
        solicitud.setPresupuestoMaximo(3500.0);

        // When
        Set<ConstraintViolation<SolicitudServicio>> violaciones = validator.validate(solicitud);

        // Then
        assertTrue(violaciones.isEmpty());
    }

    @Test
    @DisplayName("Debería validar presupuesto con decimales")
    void deberiaValidarPresupuestoConDecimales() {
        // Given
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioMock);
        solicitud.setCliente(clienteMock);
        solicitud.setDescripcionProblema("Servicio de plomería general");
        solicitud.setDireccionServicio("Calle Tucumán 600");
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(4));
        solicitud.setFranjaHoraria("Tarde");
        solicitud.setPresupuestoMaximo(2499.99);

        // When
        Set<ConstraintViolation<SolicitudServicio>> violaciones = validator.validate(solicitud);

        // Then
        assertTrue(violaciones.isEmpty());
    }

    @Test
    @DisplayName("Debería aceptar comentarios adicionales con texto descriptivo")
    void deberiaAceptarComentariosAdicionalesConTextoDescriptivo() {
        // Given
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioMock);
        solicitud.setCliente(clienteMock);
        solicitud.setDescripcionProblema("Instalación de luminarias en toda la casa");
        solicitud.setDireccionServicio("Av. Cabildo 2800");
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(11));
        solicitud.setFranjaHoraria("Mañana");
        solicitud.setPresupuestoMaximo(7000.0);
        solicitud.setComentariosAdicionales("Tengo un perro grande, por favor avisar antes de tocar el timbre. Hay estacionamiento disponible en el edificio.");

        // When
        Set<ConstraintViolation<SolicitudServicio>> violaciones = validator.validate(solicitud);

        // Then
        assertTrue(violaciones.isEmpty());
    }

    @Test
    @DisplayName("Debería validar solicitud mínima válida")
    void deberiaValidarSolicitudMinimaValida() {
        // Given - Solicitud con valores mínimos requeridos
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setServicio(servicioMock);
        solicitud.setCliente(clienteMock);
        solicitud.setDescripcionProblema("A".repeat(10)); // Mínimo 10 caracteres
        solicitud.setDireccionServicio("A".repeat(10)); // Mínimo 10 caracteres
        solicitud.setFechaSolicitada(LocalDate.now().plusDays(1));
        solicitud.setFranjaHoraria("M"); // Mínimo 1 carácter
        solicitud.setPresupuestoMaximo(0.01); // Mínimo positivo

        // When
        Set<ConstraintViolation<SolicitudServicio>> violaciones = validator.validate(solicitud);

        // Then
        assertTrue(violaciones.isEmpty());
    }
}