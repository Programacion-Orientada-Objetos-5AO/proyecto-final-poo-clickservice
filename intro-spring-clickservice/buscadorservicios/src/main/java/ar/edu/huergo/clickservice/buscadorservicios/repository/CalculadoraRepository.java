package ar.edu.huergo.clickservice.buscadorservicios.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ar.edu.huergo.clickservice.buscadorservicios.entity.OperacionCalculadora;

public interface CalculadoraRepository extends JpaRepository<OperacionCalculadora, Long> {

    List<OperacionCalculadora> findTop5ByOrderByFechaCreacionDesc();
}

