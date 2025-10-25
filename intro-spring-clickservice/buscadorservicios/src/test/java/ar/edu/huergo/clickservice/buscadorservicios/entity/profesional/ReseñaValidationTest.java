package ar.edu.huergo.clickservice.buscadorservicios.entity;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ar.edu.huergo.clickservice.buscadorservicios.entity.Profesional;
import ar.edu.huergo.clickservice.buscadorservicios.entity.security.Usuario;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@DisplayName("Tests de Validación - Entidad Reseña")
class ReseñaValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Debería validar reseña correcta sin errores")
    void deberiaValidarReseñaCorrectaSinErrores() {
        Reseña reseña = construirReseñaValida();

        Set<ConstraintViolation<Reseña>> violaciones = validator.validate(reseña);

        assertTrue(violaciones.isEmpty(),
                "No debería haber violaciones de validación para una reseña válida");
    }

    @Test
    @DisplayName("Debería fallar validación sin ordenId")
    void deberiaFallarValidacionSinOrdenId() {
        Reseña reseña = construirReseñaValida();
        reseña.setOrdenId(null);

        Set<ConstraintViolation<Reseña>> violaciones = validator.validate(reseña);

        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("ordenId")));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 6})
    @DisplayName("Debería fallar validación con rating fuera de rango")
    void deberiaFallarValidacionConRatingFueraDeRango(int ratingInvalido) {
        Reseña reseña = construirReseñaValida();
        reseña.setRating(ratingInvalido);

        Set<ConstraintViolation<Reseña>> violaciones = validator.validate(reseña);

        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("rating")));
    }

    @Test
    @DisplayName("Debería fallar validación con comentario nulo")
    void deberiaFallarValidacionConComentarioNulo() {
        Reseña reseña = construirReseñaValida();
        reseña.setComentario(null);

        Set<ConstraintViolation<Reseña>> violaciones = validator.validate(reseña);

        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("comentario")));
    }

    @Test
    @DisplayName("Debería fallar validación con comentario vacío")
    void deberiaFallarValidacionConComentarioVacio() {
        Reseña reseña = construirReseñaValida();
        reseña.setComentario("");

        Set<ConstraintViolation<Reseña>> violaciones = validator.validate(reseña);

        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("comentario")));
    }

    @Test
    @DisplayName("Debería fallar validación con comentario muy corto")
    void deberiaFallarValidacionConComentarioMuyCorto() {
        Reseña reseña = construirReseñaValida();
        reseña.setComentario("Muy corto");

        Set<ConstraintViolation<Reseña>> violaciones = validator.validate(reseña);

        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("comentario")));
    }

    @Test
    @DisplayName("Debería fallar validación sin fecha")
    void deberiaFallarValidacionSinFecha() {
        Reseña reseña = construirReseñaValida();
        reseña.setFecha(null);

        Set<ConstraintViolation<Reseña>> violaciones = validator.validate(reseña);

        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("fecha")));
    }

    @Test
    @DisplayName("Debería fallar validación sin usuario")
    void deberiaFallarValidacionSinUsuario() {
        Reseña reseña = construirReseñaValida();
        reseña.setUsuario(null);

        Set<ConstraintViolation<Reseña>> violaciones = validator.validate(reseña);

        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("usuario")));
    }

    @Test
    @DisplayName("Debería fallar validación sin profesional")
    void deberiaFallarValidacionSinProfesional() {
        Reseña reseña = construirReseñaValida();
        reseña.setProfesional(null);

        Set<ConstraintViolation<Reseña>> violaciones = validator.validate(reseña);

        assertFalse(violaciones.isEmpty());
        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("profesional")));
    }

    private Reseña construirReseñaValida() {
        Reseña reseña = new Reseña();
        reseña.setOrdenId(1L);
        reseña.setRating(4);
        reseña.setComentario("Servicio excelente y muy profesional");
        reseña.setFecha(LocalDateTime.of(2024, 1, 10, 14, 30));
        reseña.setUsuario(crearUsuarioValido());
        reseña.setProfesional(crearProfesionalBasico());
        return reseña;
    }

    private Usuario crearUsuarioValido() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Juan");
        usuario.setApellido("Pérez");
        usuario.setDni("12345678");
        usuario.setTelefono("+54 11 1234-5678");
        usuario.setCalle("Calle Falsa");
        usuario.setAltura(123);
        usuario.setUsername("juan.perez@example.com");
        usuario.setPassword("contraseñaSegura123");
        return usuario;
    }

    private Profesional crearProfesionalBasico() {
        Profesional profesional = new Profesional();
        profesional.setId(2L);
        profesional.setNombreCompleto("María López");
        profesional.setTelefono("+54 11 9876-5432");
        profesional.setDescripcion("Especialista en reparaciones");
        profesional.setCalificacionPromedio(4.5);
        profesional.setTrabajosRealizados(25);
        profesional.setDisponible(true);
        profesional.setZonaTrabajo("CABA");
        profesional.setUsuario(crearUsuarioValido());
        return profesional;
    }
}
