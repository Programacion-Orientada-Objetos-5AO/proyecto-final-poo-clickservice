package ar.edu.huergo.clickservice.buscadorservicios.entity;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@DisplayName("Tests de Validación - Entidad Comision")
class ComisionValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Debería validar comisión correcta sin errores")
    void deberiaValidarComisionCorrectaSinErrores() {
        Comision comision = new Comision(1L, 100L, 12.0, 1000.0, 120.0, LocalDate.now());

        Set<ConstraintViolation<Comision>> violaciones = validator.validate(comision);

        assertTrue(violaciones.isEmpty(),
                "No debería haber violaciones de validación para una comisión válida");
    }

    @Test
    @DisplayName("Debería fallar validación con pagoId null")
    void deberiaFallarValidacionConPagoIdNull() {
        Comision comision = new Comision();
        comision.setPagoId(null);
        comision.setTasa(12.0);
        comision.setBase(1000.0);
        comision.setMonto(120.0);
        comision.setFecha(LocalDate.now());

        Set<ConstraintViolation<Comision>> violaciones = validator.validate(comision);

        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("pagoId")));
    }

    @ParameterizedTest
    @ValueSource(doubles = {-5.0, 0.0})
    @DisplayName("Debería fallar validación con tasas no positivas")
    void deberiaFallarValidacionConTasasNoPositivas(double tasaInvalida) {
        Comision comision = new Comision();
        comision.setPagoId(100L);
        comision.setTasa(tasaInvalida);
        comision.setBase(1000.0);
        comision.setMonto(120.0);
        comision.setFecha(LocalDate.now());

        Set<ConstraintViolation<Comision>> violaciones = validator.validate(comision);

        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("tasa")));
    }

    @ParameterizedTest
    @ValueSource(doubles = {-100.0, -0.01})
    @DisplayName("Debería fallar validación con bases negativas")
    void deberiaFallarValidacionConBasesNegativas(double baseInvalida) {
        Comision comision = new Comision();
        comision.setPagoId(100L);
        comision.setTasa(10.0);
        comision.setBase(baseInvalida);
        comision.setMonto(50.0);
        comision.setFecha(LocalDate.now());

        Set<ConstraintViolation<Comision>> violaciones = validator.validate(comision);

        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("base")));
    }

    @ParameterizedTest
    @ValueSource(doubles = {-10.0, -0.1})
    @DisplayName("Debería fallar validación con montos negativos")
    void deberiaFallarValidacionConMontosNegativos(double montoInvalido) {
        Comision comision = new Comision();
        comision.setPagoId(100L);
        comision.setTasa(10.0);
        comision.setBase(100.0);
        comision.setMonto(montoInvalido);
        comision.setFecha(LocalDate.now());

        Set<ConstraintViolation<Comision>> violaciones = validator.validate(comision);

        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("monto")));
    }

    @Test
    @DisplayName("Debería fallar validación con fecha futura")
    void deberiaFallarValidacionConFechaFutura() {
        Comision comision = new Comision();
        comision.setPagoId(100L);
        comision.setTasa(10.0);
        comision.setBase(100.0);
        comision.setMonto(10.0);
        comision.setFecha(LocalDate.now().plusDays(1));

        Set<ConstraintViolation<Comision>> violaciones = validator.validate(comision);

        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("fecha")));
    }

    @Test
    @DisplayName("Debería aceptar fecha en el pasado o presente")
    void deberiaAceptarFechaPasadoOPresente() {
        Comision comisionPasada = new Comision(1L, 100L, 12.0, 1000.0, 120.0, LocalDate.now().minusDays(1));
        Comision comisionPresente = new Comision(2L, 101L, 10.0, 800.0, 80.0, LocalDate.now());

        Set<ConstraintViolation<Comision>> violacionesPasado = validator.validate(comisionPasada);
        Set<ConstraintViolation<Comision>> violacionesPresente = validator.validate(comisionPresente);

        assertTrue(violacionesPasado.isEmpty());
        assertTrue(violacionesPresente.isEmpty());
    }

    @Test
    @DisplayName("Debería detectar múltiples errores simultáneos")
    void deberiaDetectarMultiplesErroresSimultaneos() {
        Comision comisionInvalida = new Comision();
        comisionInvalida.setPagoId(null);
        comisionInvalida.setTasa(-5.0);
        comisionInvalida.setBase(-100.0);
        comisionInvalida.setMonto(-10.0);
        comisionInvalida.setFecha(LocalDate.now().plusDays(5));

        Set<ConstraintViolation<Comision>> violaciones = validator.validate(comisionInvalida);

        assertFalse(violaciones.isEmpty());
        List<String> propiedadesConError =
                violaciones.stream().map(v -> v.getPropertyPath().toString()).toList();

        assertTrue(propiedadesConError.contains("pagoId"));
        assertTrue(propiedadesConError.contains("tasa"));
        assertTrue(propiedadesConError.contains("base"));
        assertTrue(propiedadesConError.contains("monto"));
        assertTrue(propiedadesConError.contains("fecha"));
    }
}
