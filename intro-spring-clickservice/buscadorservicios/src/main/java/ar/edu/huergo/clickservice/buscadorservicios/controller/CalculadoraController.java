package ar.edu.huergo.clickservice.buscadorservicios.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.huergo.clickservice.buscadorservicios.dto.CalculadoraRequestDTO;
import ar.edu.huergo.clickservice.buscadorservicios.dto.CalculadoraResponseDTO;
import ar.edu.huergo.clickservice.buscadorservicios.dto.EstadisticasDTO;
import ar.edu.huergo.clickservice.buscadorservicios.service.CalculadoraService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/calculadora")
public class CalculadoraController {

    @Autowired
    private CalculadoraService calculadoraService;

    @PostMapping("/calcular")
    public ResponseEntity<CalculadoraResponseDTO> calcular(@Valid @RequestBody CalculadoraRequestDTO request) {
        CalculadoraResponseDTO response = calculadoraService.calcular(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/ultimos")
    public ResponseEntity<List<CalculadoraResponseDTO>> obtenerUltimos() {
        List<CalculadoraResponseDTO> ultimos = calculadoraService.obtenerUltimosCalculos();
        return ResponseEntity.ok(ultimos);
    }

    @GetMapping("/estadisticas")
    public ResponseEntity<EstadisticasDTO> obtenerEstadisticas() {
        EstadisticasDTO estadisticas = calculadoraService.obtenerEstadisticas();
        return ResponseEntity.ok(estadisticas);
    }
}
