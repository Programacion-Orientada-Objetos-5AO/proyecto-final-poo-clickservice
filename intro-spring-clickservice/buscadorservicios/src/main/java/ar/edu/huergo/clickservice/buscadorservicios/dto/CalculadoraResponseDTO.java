package ar.edu.huergo.clickservice.buscadorservicios.dto;


public record CalculadoraResponseDTO(
        Long id,
        String operacion,
        Double parametro1,
        Double parametro2,
        Double resultado
) {
}
