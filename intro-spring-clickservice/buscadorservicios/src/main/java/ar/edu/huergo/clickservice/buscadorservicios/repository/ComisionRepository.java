package ar.edu.huergo.clickservice.buscadorservicios.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.huergo.clickservice.buscadorservicios.entity.Comision;

@Repository
public interface ComisionRepository extends JpaRepository<Comision, Long> {
}
