package ar.edu.huergo.clickservice.buscadorservicios.repository;





import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ar.edu.huergo.clickservice.buscadorservicios.entity.Tarea;


@Repository
public interface TareaRepository extends JpaRepository<Tarea, Long> {
    List<Tarea> findByTituloContainingIgnoreCase(String titulo);
}
