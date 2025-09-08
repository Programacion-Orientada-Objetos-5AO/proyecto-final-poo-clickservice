package ar.edu.huergo.clickservice.buscadorservicios.entity;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@DisplayName("Tests de Validación - Entidad Servicio")
class ServicioValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Debería validar servicio correcto sin errores")
    void deberiaValidarServicioCorrectoSinErrores() {
        // Given
        Servicio servicio = new Servicio();
        servicio.setNombre("Plomería");
        servicio.setPrecioHora(25.50);

        // When
        Set<ConstraintViolation<Servicio>> violaciones = validator.validate(servicio);

        // Then
        assertTrue(violaciones.isEmpty(),
                "No debería haber violaciones de validación para un servicio válido");
    }

    @Test
    @DisplayName("Debería fallar validación con nombre null")
    void deberiaFallarValidacionConNombreNull() {
        // Given
        Servicio servicio = new Servicio();
        servicio.setNombre(null);
        servicio.setPrecioHora(25.50);

        // When
        Set<ConstraintViolation<Servicio>> violaciones = validator.validate(servicio);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("nombre")));
        assertTrue(violaciones.stream().anyMatch(v -> v.getMessage().contains("obligatorio")));
    }

    @Test
    @DisplayName("Debería fallar validación con nombre vacío")
    void deberiaFallarValidacionConNombreVacio() {
        // Given
        Servicio servicio = new Servicio();
        servicio.setNombre("");
        servicio.setPrecioHora(25.50);

        // When
        Set<ConstraintViolation<Servicio>> violaciones = validator.validate(servicio);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("nombre")));
    }

    @Test
    @DisplayName("Debería fallar validación con nombre solo espacios")
    void deberiaFallarValidacionConNombreSoloEspacios() {
        // Given
        Servicio servicio = new Servicio();
        servicio.setNombre("   ");
        servicio.setPrecioHora(25.50);

        // When
        Set<ConstraintViolation<Servicio>> violaciones = validator.validate(servicio);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("nombre")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"A", "AB"})
    @DisplayName("Debería fallar validación con nombres muy cortos")
    void deberiaFallarValidacionConNombresMuyCortos(String nombreCorto) {
        // Given
        Servicio servicio = new Servicio();
        servicio.setNombre(nombreCorto);
        servicio.setPrecioHora(25.50);

        // When
        Set<ConstraintViolation<Servicio>> violaciones = validator.validate(servicio);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("nombre")));
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getMessage().contains("entre 3 y 50 caracteres")));
    }

    @Test
    @DisplayName("Debería fallar validación con nombre muy largo")
    void deberiaFallarValidacionConNombreMuyLargo() {
        // Given
        String nombreLargo = "A".repeat(51); // 51 caracteres
        Servicio servicio = new Servicio();
        servicio.setNombre(nombreLargo);
        servicio.setPrecioHora(25.50);

        // When
        Set<ConstraintViolation<Servicio>> violaciones = validator.validate(servicio);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("nombre")));
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getMessage().contains("entre 3 y 50 caracteres")));
    }

    @Test
    @DisplayName("Debería aceptar nombres en el límite válido")
    void deberiaAceptarNombresEnLimiteValido() {
        // Given - Nombres de exactamente 3 y 50 caracteres
        String nombreMinimo = "Gas"; // 3 caracteres
        String nombreMaximo = "A".repeat(50); // 50 caracteres

        Servicio servicio1 = new Servicio();
        servicio1.setNombre(nombreMinimo);
        servicio1.setPrecioHora(25.50);

        Servicio servicio2 = new Servicio();
        servicio2.setNombre(nombreMaximo);
        servicio2.setPrecioHora(25.50);

        // When
        Set<ConstraintViolation<Servicio>> violaciones1 = validator.validate(servicio1);
        Set<ConstraintViolation<Servicio>> violaciones2 = validator.validate(servicio2);

        // Then
        assertTrue(violaciones1.isEmpty());
        assertTrue(violaciones2.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(doubles = {-1.0, -0.01, 0.0})
    @DisplayName("Debería fallar validación con precios no positivos")
    void deberiaFallarValidacionConPreciosNoPositivos(double precioInvalido) {
        // Given
        Servicio servicio = new Servicio();
        servicio.setNombre("Plomería");
        servicio.setPrecioHora(precioInvalido);

        // When
        Set<ConstraintViolation<Servicio>> violaciones = validator.validate(servicio);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("precioHora")));
        assertTrue(violaciones.stream().anyMatch(v -> v.getMessage().contains("mayor a 0")));
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.01, 1.0, 100.0, 999.99})
    @DisplayName("Debería aceptar precios positivos")
    void deberiaAceptarPreciosPositivos(double precioValido) {
        // Given
        Servicio servicio = new Servicio();
        servicio.setNombre("Electricidad");
        servicio.setPrecioHora(precioValido);

        // When
        Set<ConstraintViolation<Servicio>> violaciones = validator.validate(servicio);

        // Then
        assertTrue(violaciones.isEmpty());
    }

    @Test
    @DisplayName("Debería fallar validación con precio null")
    void deberiaFallarValidacionConPrecioNull() {
        // Given
        Servicio servicio = new Servicio();
        servicio.setNombre("Carpintería");
        servicio.setPrecioHora(null);

        // When
        Set<ConstraintViolation<Servicio>> violaciones = validator.validate(servicio);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("precioHora")));
        assertTrue(violaciones.stream().anyMatch(v -> v.getMessage().contains("obligatorio")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Plomería", "Gas", "Electricidad", "Albañilería", "Jardinería", "Carpintería"})
    @DisplayName("Debería aceptar nombres de servicios comunes")
    void deberiaAceptarNombresDeServiciosComunes(String nombreValido) {
        // Given
        Servicio servicio = new Servicio();
        servicio.setNombre(nombreValido);
        servicio.setPrecioHora(30.00);

        // When
        Set<ConstraintViolation<Servicio>> violaciones = validator.validate(servicio);

        // Then
        assertTrue(violaciones.isEmpty(), "El nombre '" + nombreValido + "' debería ser válido");
    }

    @Test
    @DisplayName("Debería validar múltiples errores simultáneamente")
    void deberiaValidarMultiplesErroresSimultaneamente() {
        // Given - Servicio con múltiples errores
        Servicio servicioInvalido = new Servicio();
        servicioInvalido.setNombre(""); // Nombre vacío
        servicioInvalido.setPrecioHora(-5.0); // Precio negativo

        // When
        Set<ConstraintViolation<Servicio>> violaciones = validator.validate(servicioInvalido);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.size() >= 2); // Al menos 2 errores

        List<String> propiedadesConError =
                violaciones.stream().map(v -> v.getPropertyPath().toString()).toList();

        assertTrue(propiedadesConError.contains("nombre"));
        assertTrue(propiedadesConError.contains("precioHora"));
    }

    @Test
    @DisplayName("Debería validar servicio con constructor completo")
    void deberiaValidarServicioConConstructorCompleto() {
        // Given
        Servicio servicio = new Servicio(1L, "Plomería", 35.50);

        // When
        Set<ConstraintViolation<Servicio>> violaciones = validator.validate(servicio);

        // Then
        assertTrue(violaciones.isEmpty());
    }

    @Test
    @DisplayName("Debería manejar nombres con caracteres especiales")
    void deberiaManejarNombresConCaracteresEspeciales() {
        // Given
        Servicio servicio = new Servicio();
        servicio.setNombre("Aire Acondicionado");
        servicio.setPrecioHora(45.00);

        // When
        Set<ConstraintViolation<Servicio>> violaciones = validator.validate(servicio);

        // Then
        assertTrue(violaciones.isEmpty(), "Los nombres con espacios deberían ser válidos");
    }
}