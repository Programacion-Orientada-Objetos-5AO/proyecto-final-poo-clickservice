package ar.edu.huergo.clickservice.buscadorservicios.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.huergo.clickservice.buscadorservicios.entity.Profesional;

@Repository
public interface ProfesionalRepository extends JpaRepository<Profesional, Long> {
    
    // Buscar profesional por ID de usuario
    Optional<Profesional> findByUsuarioId(Long usuarioId);
    
    // Buscar profesionales disponibles
    List<Profesional> findByDisponibleTrue();
    
    // Buscar profesionales por servicio (usando la tabla intermedia)
    List<Profesional> findByServiciosIdAndDisponibleTrue(Long servicioId);
}