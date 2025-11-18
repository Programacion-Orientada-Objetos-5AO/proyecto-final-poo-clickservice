package ar.edu.huergo.clickservice.buscadorservicios.mapper;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import ar.edu.huergo.clickservice.buscadorservicios.dto.CalculadoraRequestDTO;
import ar.edu.huergo.clickservice.buscadorservicios.dto.CalculadoraResponseDTO;
import ar.edu.huergo.clickservice.buscadorservicios.entity.OperacionCalculadora;

@Component
public class CalculadoraMapper {

    public OperacionCalculadora toEntity(CalculadoraRequestDTO dto, double resultado, String operacionNormalizada) {
        OperacionCalculadora entity = new OperacionCalculadora();
        entity.setOperacion(operacionNormalizada);
        entity.setParametro1(dto.parametro1());
        entity.setParametro2(dto.parametro2());
        entity.setResultado(resultado);
        return entity;
    }

    public CalculadoraResponseDTO toDto(OperacionCalculadora entity) {
        if (entity == null) {
            return null;
        }

        return new CalculadoraResponseDTO(
                entity.getId(),
                entity.getOperacion(),
                entity.getParametro1(),
                entity.getParametro2(),
                entity.getResultado()
        );
    }

    public List<CalculadoraResponseDTO> toDtoList(List<OperacionCalculadora> entities) {
        if (entities == null) {
            return new ArrayList<>();
        }
        return entities.stream().map(this::toDto).collect(Collectors.toList());
    }
}

