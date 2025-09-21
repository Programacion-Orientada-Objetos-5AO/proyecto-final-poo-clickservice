package ar.edu.huergo.clickservice.buscadorservicios.dto.security;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * DTO para el registro de un nuevo profesional
 * Incluye tanto datos de usuario como datos específicos del profesional
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrarProfesionalDTO {

    // Datos de usuario
    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Email(message = "El formato del email no es válido")
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{16,}$", 
             message = "La contraseña debe tener al menos 16 caracteres, una mayúscula, una minúscula, un número y un carácter especial")
    private String password;

    // Datos específicos del profesional
    @NotBlank(message = "El nombre completo es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre completo debe tener entre 3 y 100 caracteres")
    private String nombreCompleto;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[0-9+\\-\\s()]{8,20}$", message = "El formato del teléfono no es válido")
    private String telefono;

    @Size(max = 500, message = "La descripción no puede exceder los 500 caracteres")
    private String descripcion;

    @Size(max = 200, message = "La zona de trabajo no puede exceder los 200 caracteres")
    private String zonaTrabajo;

    // IDs de los servicios que puede ofrecer el profesional
    @NotEmpty(message = "Debe seleccionar al menos un servicio")
    private Set<Long> serviciosIds;
}