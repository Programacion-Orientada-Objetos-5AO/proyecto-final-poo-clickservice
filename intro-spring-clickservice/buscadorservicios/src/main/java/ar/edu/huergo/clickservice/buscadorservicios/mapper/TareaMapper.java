package ar.edu.huergo.clickservice.buscadorservicios.mapper;

 






import org.springframework.stereotype.Component;
import ar.edu.huergo.clickservice.buscadorservicios.dto.TareaDTO;
import ar.edu.huergo.clickservice.buscadorservicios.entity.Tarea;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


//Un Mapper es una clase que se encarga de convertir un objeto de un tipo a otro.
//En este caso, se encarga de convertir un objeto Plato a un objeto PlatoDTO y viceversa.
//Esto es útil para evitar que el controlador se encargue de la conversión de objetos.
@Component
public class TareaMapper {


    /**
     * 
     */
    public TareaDTO toDTO(Tarea tarea) {
        if (tarea == null) {
            return null;
        }
        return new TareaDTO(
            tarea.getId(),
            tarea.getTitulo(),
            tarea.getDescripcion(),
            tarea.getCreador(),
            tarea.getCompletada()
        );
    }


    /**
     * Convierte un tareAdto a entidad tarea
     */
    public Tarea toEntity(TareaDTO dto) {
        if (dto == null) {
            return null;
        }
        Tarea tarea = new Tarea();
        tarea.setTitulo(dto.titulo());
        tarea.setDescripcion(dto.descripcion());
        tarea.setCreador(dto.creador());
        tarea.setCompletada(dto.completada());
        return tarea;


       


       
    }


    /**
     * Convierte una lista de entidades Plato a lista de PlatoDTO
     */
    public List<TareaDTO> toDTOList(List<Tarea> tareas) {
        if (tareas == null) {
            return new ArrayList<>();
        }
        return tareas.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
