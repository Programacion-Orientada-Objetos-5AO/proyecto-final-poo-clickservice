package ar.edu.huergo.clickservice.buscadorservicios.controller;




import java.net.URI;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


import ar.edu.huergo.clickservice.buscadorservicios.service.TareaService;
import jakarta.validation.Valid;
import ar.edu.huergo.clickservice.buscadorservicios.dto.TareaDTO;
import ar.edu.huergo.clickservice.buscadorservicios.mapper.TareaMapper;


@RestController
@RequestMapping("/api/tareas")


public class TareaController {


    @Autowired
    private TareaService tareaService;


   
    @Autowired
    private TareaMapper tareaMapper;


    @GetMapping
    public ResponseEntity<List<TareaDTO>> obtenerTodosLosTareas() {
        return ResponseEntity.ok(tareaMapper.toDTOList(tareaService.obtenerTodosLosTareas()));
    }


    @PostMapping
    public ResponseEntity<TareaDTO> crearTarea(@RequestBody @Valid TareaDTO tareaDTO) {
        TareaDTO tareaCreado = tareaMapper.toDTO(tareaService.crearTarea(tareaMapper.toEntity(tareaDTO)));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(tareaCreado.id())
            .toUri();
        return ResponseEntity.created(location).body(tareaCreado);
    }


     @GetMapping("/{id}")
    public ResponseEntity<TareaDTO> obtenerTareaPorId(@PathVariable Long id) {
        return ResponseEntity.ok(tareaMapper.toDTO(tareaService.obtenerTareaPorId(id)));
    }


    @PutMapping("/{id}")
    public ResponseEntity<TareaDTO> actualizarProducto(@PathVariable Long id, @RequestBody @Valid TareaDTO tareaDTO) {
        return ResponseEntity.ok(tareaMapper.toDTO( tareaService.actualizarTarea(  id,tareaMapper.toEntity(tareaDTO))) );
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTarea(@PathVariable Long id) {
        tareaService.eliminarTarea(id);
        return ResponseEntity.noContent().build(); }
   
    @GetMapping("/titulo")
    public ResponseEntity<List<TareaDTO>> obtenerTareaPorTitulo(@RequestParam String titulo) {
        return ResponseEntity.ok(tareaMapper.toDTOList(tareaService.obtenerTareaPorTitulo(titulo)));
    }



   
   
    }
   












