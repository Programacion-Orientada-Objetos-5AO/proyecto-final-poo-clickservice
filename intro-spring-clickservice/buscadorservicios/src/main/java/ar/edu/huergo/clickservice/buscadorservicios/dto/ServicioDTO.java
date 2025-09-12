package ar.edu.huergo.clickservice.buscadorservicios.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO para transferencia de datos de Servicio
 * Representa los datos que se exponen en la API REST
 */
@Data
@AllArgsConstructor
public abstract class ServicioDTO {
    Long id;
    @NotBlank(message = "El nombre del servicio es obligatorio")
    @Size(min = 3, max = 50, message = "El nombre del servicio debe tener entre 3 y 50 caracteres")
    String nombre;
    @Positive(message = "El precio por hora debe ser positivo")
    @NotNull(message = "El precio por hora no puede ser nulo")
    Double precioHora;
}