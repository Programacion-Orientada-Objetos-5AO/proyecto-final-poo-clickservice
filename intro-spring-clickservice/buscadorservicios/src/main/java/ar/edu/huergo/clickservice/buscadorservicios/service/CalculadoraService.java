package ar.edu.huergo.clickservice.buscadorservicios.service;


import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.huergo.clickservice.buscadorservicios.dto.CalculadoraRequestDTO;
import ar.edu.huergo.clickservice.buscadorservicios.dto.CalculadoraResponseDTO;
import ar.edu.huergo.clickservice.buscadorservicios.dto.EstadisticasDTO;
import ar.edu.huergo.clickservice.buscadorservicios.entity.OperacionCalculadora;
import ar.edu.huergo.clickservice.buscadorservicios.mapper.CalculadoraMapper;
import ar.edu.huergo.clickservice.buscadorservicios.repository.CalculadoraRepository;

@Service
public class CalculadoraService {

    private static final String OPERACION_SUMA = "SUMA";
    private static final String OPERACION_RESTA = "RESTA";
    private static final String OPERACION_MULTIPLICACION = "MULTIPLICACION";
    private static final String OPERACION_DIVISION = "DIVISION";

    @Autowired
    private CalculadoraRepository calculadoraRepository;

    @Autowired
    private CalculadoraMapper calculadoraMapper;

    @Transactional
    public CalculadoraResponseDTO calcular(CalculadoraRequestDTO request) {
        String operacionNormalizada = normalizarOperacion(request.operacion());
        double resultado = ejecutarOperacion(operacionNormalizada, request.parametro1(), request.parametro2());

        OperacionCalculadora operacion = calculadoraMapper.toEntity(request, resultado, operacionNormalizada);
        OperacionCalculadora guardada = calculadoraRepository.save(operacion);

        return calculadoraMapper.toDto(guardada);
    }

    @Transactional(readOnly = true)
    public List<CalculadoraResponseDTO> obtenerUltimosCalculos() {
        List<OperacionCalculadora> ultimos = calculadoraRepository.findTop5ByOrderByFechaCreacionDesc();
        return calculadoraMapper.toDtoList(ultimos);
    }

    @Transactional(readOnly = true)
    public EstadisticasDTO obtenerEstadisticas() {
        List<OperacionCalculadora> operaciones = calculadoraRepository.findAll();

        Map<String, Integer> conteo = new HashMap<>();
        double sumaResultados = 0.0;

        for (OperacionCalculadora op : operaciones) {
            conteo.merge(op.getOperacion(), 1, Integer::sum);
            sumaResultados += op.getResultado();
        }

        Optional<Map.Entry<String, Integer>> masFrecuente = conteo.entrySet().stream()
                .max(Comparator.comparing(Map.Entry::getValue));

        String operacionMasUsada = masFrecuente.map(Map.Entry::getKey).orElse(null);
        double promedio = operaciones.isEmpty() ? 0.0 : sumaResultados / operaciones.size();

        return new EstadisticasDTO(
                operacionMasUsada,
                promedio,
                operaciones.size(),
                conteo
        );
    }

    private String normalizarOperacion(String operacion) {
        if (operacion == null) {
            throw new IllegalArgumentException("La operacion no puede ser nuld");
        }

        return switch (operacion.trim().toLowerCase()) {
            case "+", "suma", "sumar" -> OPERACION_SUMA;
            case "-", "resta", "restar" -> OPERACION_RESTA;
            case "*", "multiplicacion", "multiplicar", "x" -> OPERACION_MULTIPLICACION;
            case "/", "division", "dividir" -> OPERACION_DIVISION;
            default -> throw new IllegalArgumentException("opreracion no soportada: " + operacion);
        };
    }

    private double ejecutarOperacion(String operacion, double parametro1, double parametro2) {
        return switch (operacion) {
            case OPERACION_SUMA -> parametro1 + parametro2;
            case OPERACION_RESTA -> parametro1 - parametro2;
            case OPERACION_MULTIPLICACION -> parametro1 * parametro2;
            case OPERACION_DIVISION -> {
                if (parametro2 == 0) {
                    throw new IllegalArgumentException("no se puede dividir por cero");
                }
                yield parametro1 / parametro2;
            }
            default -> throw new IllegalStateException("operacion no contemplada: " + operacion);
        };
    }
}


