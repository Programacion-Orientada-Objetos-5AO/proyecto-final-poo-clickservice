package ar.edu.huergo.clickservice.buscadorservicios.entity;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@DisplayName("Tests de Validación - Entidad AgendaSlot")
class AgendaSlotValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Debería validar un agenda slot correcto")
    void deberiaValidarAgendaSlotCorrecto() {
        AgendaSlot agendaSlot = construirAgendaSlotBasico();

        Set<ConstraintViolation<AgendaSlot>> violaciones = validator.validate(agendaSlot);

        assertTrue(violaciones.isEmpty());
    }

    @Test
    @DisplayName("Debería fallar validación sin profesional")
    void deberiaFallarValidacionSinProfesional() {
        AgendaSlot agendaSlot = construirAgendaSlotBasico();
        agendaSlot.setProfesional(null);

        Set<ConstraintViolation<AgendaSlot>> violaciones = validator.validate(agendaSlot);

        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("profesional")));
    }

    @Test
    @DisplayName("Debería fallar validación sin fechas obligatorias")
    void deberiaFallarValidacionSinFechasObligatorias() {
        AgendaSlot agendaSlot = construirAgendaSlotBasico();
        agendaSlot.setFechaInicio(null);
        agendaSlot.setFechaFin(null);

        Set<ConstraintViolation<AgendaSlot>> violaciones = validator.validate(agendaSlot);

        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("fechaInicio")));
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("fechaFin")));
    }

    @Test
    @DisplayName("Debería fallar validación cuando la fecha fin es anterior")
    void deberiaFallarValidacionConFechaFinAnterior() {
        AgendaSlot agendaSlot = construirAgendaSlotBasico();
        agendaSlot.setFechaFin(agendaSlot.getFechaInicio().minusHours(1));

        Set<ConstraintViolation<AgendaSlot>> violaciones = validator.validate(agendaSlot);

        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getMessage().contains("posterior a la fecha de inicio")));
    }

    private AgendaSlot construirAgendaSlotBasico() {
        Profesional profesional = new Profesional();
        profesional.setId(1L);
        profesional.setNombreCompleto("Profesional Demo");

        AgendaSlot agendaSlot = new AgendaSlot();
        agendaSlot.setProfesional(profesional);
        agendaSlot.setFechaInicio(LocalDateTime.of(2024, 10, 20, 9, 0));
        agendaSlot.setFechaFin(LocalDateTime.of(2024, 10, 20, 11, 0));
        agendaSlot.setDisponible(true);
        return agendaSlot;
    }
}
