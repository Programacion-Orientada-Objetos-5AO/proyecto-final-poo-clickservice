package ar.edu.huergo.clickservice.buscadorservicios.dto;

import java.util.Map;

public record EstadisticasDTO(
        String operacionMasUsada,
        Double promedioResultados,
        Integer totalCalculos,
        Map<String, Integer> calculosPorOperacion
) {
}

