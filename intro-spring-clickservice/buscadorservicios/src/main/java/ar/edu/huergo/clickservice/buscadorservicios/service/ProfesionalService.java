package ar.edu.huergo.clickservice.buscadorservicios.service;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.huergo.clickservice.buscadorservicios.entity.Profesional;
import ar.edu.huergo.clickservice.buscadorservicios.entity.Servicio;
import ar.edu.huergo.clickservice.buscadorservicios.repository.ProfesionalRepository;
import ar.edu.huergo.clickservice.buscadorservicios.repository.ServicioRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class ProfesionalService {

    @Autowired
    private ProfesionalRepository profesionalRepository;

    @Autowired
    private ServicioRepository servicioRepository;

    public List<Profesional> obtenerTodosLosProfesionales() {
        return profesionalRepository.findAll();
    }

    public Profesional obtenerProfesionalPorId(Long id) {
        return profesionalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Profesional no encontrado con id: " + id));
    }

    public Profesional crearProfesional(Profesional profesional) {
        return profesionalRepository.save(profesional);
    }

    public Profesional actualizarProfesional(Long id, Profesional profesionalActualizado) {
        Profesional profesionalExistente = obtenerProfesionalPorId(id);
        
        profesionalExistente.setNombreCompleto(profesionalActualizado.getNombreCompleto());
        profesionalExistente.setTelefono(profesionalActualizado.getTelefono());
        profesionalExistente.setDescripcion(profesionalActualizado.getDescripcion());
        profesionalExistente.setDisponible(profesionalActualizado.getDisponible());
        profesionalExistente.setZonaTrabajo(profesionalActualizado.getZonaTrabajo());
        
        return profesionalRepository.save(profesionalExistente);
    }

    public void eliminarProfesional(Long id) {
        if (!profesionalRepository.existsById(id)) {
            throw new EntityNotFoundException("Profesional no encontrado con id: " + id);
        }
        profesionalRepository.deleteById(id);
    }

    public List<Profesional> obtenerProfesionalesPorServicio(Long servicioId) {
        return profesionalRepository.findByServicioIdAndDisponibleTrue(servicioId);
    }

    public List<Profesional> obtenerProfesionalesDisponibles() {
        return profesionalRepository.findByDisponibleTrue();
    }

    public Profesional asignarServicios(Long profesionalId, Set<Long> serviciosIds) {
        Profesional profesional = obtenerProfesionalPorId(profesionalId);
        Set<Servicio> servicios = Set.copyOf(servicioRepository.findAllById(serviciosIds));
        profesional.setServicios(servicios);
        return profesionalRepository.save(profesional);
    }

    public Profesional obtenerProfesionalPorUsuarioId(Long usuarioId) {
        return profesionalRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Profesional no encontrado para el usuario con id: " + usuarioId));
    }
}