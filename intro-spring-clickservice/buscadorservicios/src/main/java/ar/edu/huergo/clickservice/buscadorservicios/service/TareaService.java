package ar.edu.huergo.clickservice.buscadorservicios.service;










import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import ar.edu.huergo.clickservice.buscadorservicios.entity.Tarea;
import ar.edu.huergo.clickservice.buscadorservicios.repository.TareaRepository;
import jakarta.persistence.EntityNotFoundException;


@Service
public class TareaService {
    @Autowired
    private TareaRepository tareaRepository;


    public List<Tarea> obtenerTodosLosTareas() {
        return tareaRepository.findAll();
    }


    public Tarea obtenerTareaPorId(Long id) throws EntityNotFoundException {
        return tareaRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Tarea no encontrada"));
    }


    public Tarea crearTarea(Tarea tarea) {
        return tareaRepository.save(tarea);
    }


    public Tarea actualizarTarea(Long id, Tarea tarea) throws EntityNotFoundException {
        Tarea tareaExistente = obtenerTareaPorId(id);
       tareaExistente.setTitulo(tarea.getTitulo());
        tareaExistente.setDescripcion(tarea.getDescripcion());
        tareaExistente.setCreador(tarea.getCreador());
        tareaExistente.setCompletada(tarea.getCompletada());
        return tareaRepository.save(tareaExistente);
    }
   
    public void eliminarTarea(Long id) throws EntityNotFoundException {
         Tarea tarea = obtenerTareaPorId(id);
       tareaRepository.delete(tarea);
    }


    public List<Tarea> obtenerTareaPorTitulo(String titulo) {
        return tareaRepository.findByTituloContainingIgnoreCase(titulo);
    }




}
