package ar.edu.huergo.clickservice.buscadorservicios.entity;

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

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@DisplayName("Tests de Validación - Entidad Profesional")
class ProfesionalValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Debería validar profesional correcto sin errores")
    void deberiaValidarProfesionalCorrectoSinErrores() {
        // Given
        Profesional profesional = new Profesional();
        profesional.setNombreCompleto("Juan Carlos Pérez");
        profesional.setTelefono("11-2345-6789");
        profesional.setDescripcion("Profesional con 10 años de experiencia");
        profesional.setCalificacionPromedio(4.5);
        profesional.setTrabajosRealizados(25);
        profesional.setDisponible(true);
        profesional.setZonaTrabajo("Zona Norte");

        // When
        Set<ConstraintViolation<Profesional>> violaciones = validator.validate(profesional);

        // Then
        assertTrue(violaciones.isEmpty(),
                "No debería haber violaciones de validación para un profesional válido");
    }

    @Test
    @DisplayName("Debería fallar validación con nombre completo null")
    void deberiaFallarValidacionConNombreCompletoNull() {
        // Given
        Profesional profesional = new Profesional();
        profesional.setNombreCompleto(null);
        profesional.setTelefono("11-2345-6789");

        // When
        Set<ConstraintViolation<Profesional>> violaciones = validator.validate(profesional);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("nombreCompleto")));
        assertTrue(violaciones.stream().anyMatch(v -> v.getMessage().contains("obligatorio")));
    }

    @Test
    @DisplayName("Debería fallar validación con nombre completo vacío")
    void deberiaFallarValidacionConNombreCompletoVacio() {
        // Given
        Profesional profesional = new Profesional();
        profesional.setNombreCompleto("");
        profesional.setTelefono("11-2345-6789");

        // When
        Set<ConstraintViolation<Profesional>> violaciones = validator.validate(profesional);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("nombreCompleto")));
    }

    @Test
    @DisplayName("Debería fallar validación con nombre completo solo espacios")
    void deberiaFallarValidacionConNombreCompletoSoloEspacios() {
        // Given
        Profesional profesional = new Profesional();
        profesional.setNombreCompleto("   ");
        profesional.setTelefono("11-2345-6789");

        // When
        Set<ConstraintViolation<Profesional>> violaciones = validator.validate(profesional);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("nombreCompleto")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"A", "AB"})
    @DisplayName("Debería fallar validación con nombres muy cortos")
    void deberiaFallarValidacionConNombresMuyCortos(String nombreCorto) {
        // Given
        Profesional profesional = new Profesional();
        profesional.setNombreCompleto(nombreCorto);
        profesional.setTelefono("11-2345-6789");

        // When
        Set<ConstraintViolation<Profesional>> violaciones = validator.validate(profesional);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("nombreCompleto")));
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getMessage().contains("entre 3 y 100 caracteres")));
    }

    @Test
    @DisplayName("Debería fallar validación con nombre muy largo")
    void deberiaFallarValidacionConNombreMuyLargo() {
        // Given
        String nombreLargo = "A".repeat(101); // 101 caracteres
        Profesional profesional = new Profesional();
        profesional.setNombreCompleto(nombreLargo);
        profesional.setTelefono("11-2345-6789");

        // When
        Set<ConstraintViolation<Profesional>> violaciones = validator.validate(profesional);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("nombreCompleto")));
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getMessage().contains("entre 3 y 100 caracteres")));
    }

    @Test
    @DisplayName("Debería aceptar nombres en el límite válido")
    void deberiaAceptarNombresEnLimiteValido() {
        // Given - Nombres de exactamente 3 y 100 caracteres
        String nombreMinimo = "Ana"; // 3 caracteres
        String nombreMaximo = "A".repeat(100); // 100 caracteres

        Profesional profesional1 = new Profesional();
        profesional1.setNombreCompleto(nombreMinimo);
        profesional1.setTelefono("11-2345-6789");

        Profesional profesional2 = new Profesional();
        profesional2.setNombreCompleto(nombreMaximo);
        profesional2.setTelefono("11-2345-6789");

        // When
        Set<ConstraintViolation<Profesional>> violaciones1 = validator.validate(profesional1);
        Set<ConstraintViolation<Profesional>> violaciones2 = validator.validate(profesional2);

        // Then
        assertTrue(violaciones1.isEmpty());
        assertTrue(violaciones2.isEmpty());
    }

    @Test
    @DisplayName("Debería fallar validación con teléfono null")
    void deberiaFallarValidacionConTelefonoNull() {
        // Given
        Profesional profesional = new Profesional();
        profesional.setNombreCompleto("Juan Pérez");
        profesional.setTelefono(null);

        // When
        Set<ConstraintViolation<Profesional>> violaciones = validator.validate(profesional);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("telefono")));
        assertTrue(violaciones.stream().anyMatch(v -> v.getMessage().contains("obligatorio")));
    }

    @Test
    @DisplayName("Debería fallar validación con teléfono vacío")
    void deberiaFallarValidacionConTelefonoVacio() {
        // Given
        Profesional profesional = new Profesional();
        profesional.setNombreCompleto("Juan Pérez");
        profesional.setTelefono("");

        // When
        Set<ConstraintViolation<Profesional>> violaciones = validator.validate(profesional);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("telefono")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"11-2345-6789", "011-4567-8900", "+54 11 2345 6789", "(011) 4567-8900", "1122334455"})
    @DisplayName("Debería aceptar formatos de teléfono válidos")
    void deberiaAceptarFormatosTelefonoValidos(String telefonoValido) {
        // Given
        Profesional profesional = new Profesional();
        profesional.setNombreCompleto("María García");
        profesional.setTelefono(telefonoValido);

        // When
        Set<ConstraintViolation<Profesional>> violaciones = validator.validate(profesional);

        // Then
        assertTrue(violaciones.isEmpty(), "El teléfono '" + telefonoValido + "' debería ser válido");
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc123", "123", "++54-11-123", "telefono", "12345678901234567890123"})
    @DisplayName("Debería fallar validación con formatos de teléfono inválidos")
    void deberiaFallarValidacionConFormatosTelefonoInvalidos(String telefonoInvalido) {
        // Given
        Profesional profesional = new Profesional();
        profesional.setNombreCompleto("Carlos López");
        profesional.setTelefono(telefonoInvalido);

        // When
        Set<ConstraintViolation<Profesional>> violaciones = validator.validate(profesional);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("telefono")));
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getMessage().contains("formato del teléfono no es válido")));
    }

    @Test
    @DisplayName("Debería fallar validación con descripción muy larga")
    void deberiaFallarValidacionConDescripcionMuyLarga() {
        // Given
        String descripcionLarga = "A".repeat(501); // 501 caracteres
        Profesional profesional = new Profesional();
        profesional.setNombreCompleto("Ana Martínez");
        profesional.setTelefono("11-2345-6789");
        profesional.setDescripcion(descripcionLarga);

        // When
        Set<ConstraintViolation<Profesional>> violaciones = validator.validate(profesional);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("descripcion")));
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getMessage().contains("no puede exceder los 500 caracteres")));
    }

    @Test
    @DisplayName("Debería aceptar descripción en el límite válido")
    void deberiaAceptarDescripcionEnLimiteValido() {
        // Given
        String descripcionMaxima = "A".repeat(500); // 500 caracteres exactos
        Profesional profesional = new Profesional();
        profesional.setNombreCompleto("Luis Rodríguez");
        profesional.setTelefono("11-2345-6789");
        profesional.setDescripcion(descripcionMaxima);

        // When
        Set<ConstraintViolation<Profesional>> violaciones = validator.validate(profesional);

        // Then
        assertTrue(violaciones.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(doubles = {-1.0, -0.5, 5.1, 10.0})
    @DisplayName("Debería fallar validación con calificación fuera de rango")
    void deberiaFallarValidacionConCalificacionFueraDeRango(double calificacionInvalida) {
        // Given
        Profesional profesional = new Profesional();
        profesional.setNombreCompleto("Pedro González");
        profesional.setTelefono("11-2345-6789");
        profesional.setCalificacionPromedio(calificacionInvalida);

        // When
        Set<ConstraintViolation<Profesional>> violaciones = validator.validate(profesional);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("calificacionPromedio")));
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, 2.5, 4.8, 5.0})
    @DisplayName("Debería aceptar calificaciones válidas")
    void deberiaAceptarCalificacionesValidas(double calificacionValida) {
        // Given
        Profesional profesional = new Profesional();
        profesional.setNombreCompleto("Sofia Fernández");
        profesional.setTelefono("11-2345-6789");
        profesional.setCalificacionPromedio(calificacionValida);

        // When
        Set<ConstraintViolation<Profesional>> violaciones = validator.validate(profesional);

        // Then
        assertTrue(violaciones.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -5, -10})
    @DisplayName("Debería fallar validación con trabajos realizados negativos")
    void deberiaFallarValidacionConTrabajosRealizadosNegativos(int trabajosNegativos) {
        // Given
        Profesional profesional = new Profesional();
        profesional.setNombreCompleto("Diego Torres");
        profesional.setTelefono("11-2345-6789");
        profesional.setTrabajosRealizados(trabajosNegativos);

        // When
        Set<ConstraintViolation<Profesional>> violaciones = validator.validate(profesional);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("trabajosRealizados")));
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getMessage().contains("no puede ser negativo")));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 10, 100, 1000})
    @DisplayName("Debería aceptar trabajos realizados válidos")
    void deberiaAceptarTrabajosRealizadosValidos(int trabajosValidos) {
        // Given
        Profesional profesional = new Profesional();
        profesional.setNombreCompleto("Laura Méndez");
        profesional.setTelefono("11-2345-6789");
        profesional.setTrabajosRealizados(trabajosValidos);

        // When
        Set<ConstraintViolation<Profesional>> violaciones = validator.validate(profesional);

        // Then
        assertTrue(violaciones.isEmpty());
    }

    @Test
    @DisplayName("Debería fallar validación con zona de trabajo muy larga")
    void deberiaFallarValidacionConZonaTrabajoMuyLarga() {
        // Given
        String zonaLarga = "A".repeat(201); // 201 caracteres
        Profesional profesional = new Profesional();
        profesional.setNombreCompleto("Roberto Silva");
        profesional.setTelefono("11-2345-6789");
        profesional.setZonaTrabajo(zonaLarga);

        // When
        Set<ConstraintViolation<Profesional>> violaciones = validator.validate(profesional);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("zonaTrabajo")));
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getMessage().contains("no puede exceder los 200 caracteres")));
    }

    @Test
    @DisplayName("Debería aceptar zona de trabajo en el límite válido")
    void deberiaAceptarZonaTrabajoEnLimiteValido() {
        // Given
        String zonaMaxima = "A".repeat(200); // 200 caracteres exactos
        Profesional profesional = new Profesional();
        profesional.setNombreCompleto("Carmen Ruiz");
        profesional.setTelefono("11-2345-6789");
        profesional.setZonaTrabajo(zonaMaxima);

        // When
        Set<ConstraintViolation<Profesional>> violaciones = validator.validate(profesional);

        // Then
        assertTrue(violaciones.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Juan Carlos Pérez", "María Fernanda López", "José Antonio García", "Ana Sofía Martínez"})
    @DisplayName("Debería aceptar nombres completos comunes")
    void deberiaAceptarNombresCompletosComunes(String nombreValido) {
        // Given
        Profesional profesional = new Profesional();
        profesional.setNombreCompleto(nombreValido);
        profesional.setTelefono("11-2345-6789");

        // When
        Set<ConstraintViolation<Profesional>> violaciones = validator.validate(profesional);

        // Then
        assertTrue(violaciones.isEmpty(), "El nombre '" + nombreValido + "' debería ser válido");
    }

    @Test
    @DisplayName("Debería validar múltiples errores simultáneamente")
    void deberiaValidarMultiplesErroresSimultaneamente() {
        // Given - Profesional con múltiples errores
        Profesional profesionalInvalido = new Profesional();
        profesionalInvalido.setNombreCompleto(""); // Nombre vacío
        profesionalInvalido.setTelefono("abc"); // Teléfono inválido
        profesionalInvalido.setCalificacionPromedio(-1.0); // Calificación inválida
        profesionalInvalido.setTrabajosRealizados(-5); // Trabajos negativos

        // When
        Set<ConstraintViolation<Profesional>> violaciones = validator.validate(profesionalInvalido);

        // Then
        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.size() >= 4); // Al menos 4 errores

        List<String> propiedadesConError =
                violaciones.stream().map(v -> v.getPropertyPath().toString()).toList();

        assertTrue(propiedadesConError.contains("nombreCompleto"));
        assertTrue(propiedadesConError.contains("telefono"));
        assertTrue(propiedadesConError.contains("calificacionPromedio"));
        assertTrue(propiedadesConError.contains("trabajosRealizados"));
    }

    @Test
    @DisplayName("Debería validar profesional con valores por defecto")
    void deberiaValidarProfesionalConValoresPorDefecto() {
        // Given
        Profesional profesional = new Profesional();
        profesional.setNombreCompleto("Alejandro Castro");
        profesional.setTelefono("11-2345-6789");
        // Los valores por defecto se establecen en @PrePersist
        profesional.onCreate();

        // When
        Set<ConstraintViolation<Profesional>> violaciones = validator.validate(profesional);

        // Then
        assertTrue(violaciones.isEmpty());
        assertTrue(profesional.getCalificacionPromedio() >= 0.0);
        assertTrue(profesional.getTrabajosRealizados() >= 0);
        assertTrue(profesional.getDisponible());
    }

    @Test
    @DisplayName("Debería validar profesional completo con constructor")
    void deberiaValidarProfesionalCompletoConConstructor() {
        // Given
        Profesional profesional = new Profesional(
                1L, null, "Valeria Morales", "11-9876-5432", 
                "Especialista en reparaciones", 4.8, 50, true, 
                null, "Zona Sur", LocalDateTime.now()
        );

        // When
        Set<ConstraintViolation<Profesional>> violaciones = validator.validate(profesional);

        // Then
        assertTrue(violaciones.isEmpty());
    }

    @Test
    @DisplayName("Debería manejar nombres con caracteres especiales")
    void deberiaManejarNombresConCaracteresEspeciales() {
        // Given
        Profesional profesional = new Profesional();
        profesional.setNombreCompleto("José María Fernández-López");
        profesional.setTelefono("11-2345-6789");

        // When
        Set<ConstraintViolation<Profesional>> violaciones = validator.validate(profesional);

        // Then
        assertTrue(violaciones.isEmpty(), "Los nombres con caracteres especiales deberían ser válidos");
    }
}