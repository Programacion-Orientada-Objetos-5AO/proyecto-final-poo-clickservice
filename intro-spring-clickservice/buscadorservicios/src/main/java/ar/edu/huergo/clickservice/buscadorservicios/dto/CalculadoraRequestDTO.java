package ar.edu.huergo.clickservice.buscadorservicios.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CalculadoraRequestDTO(
        @NotBlank(message = "La operacion es obligatoria") String operacion,
        @NotNull(message = "El primer parametro es obligatorio") Double parametro1,
        @NotNull(message = "El segundo parametro es obligatorio") Double parametro2
) {
}