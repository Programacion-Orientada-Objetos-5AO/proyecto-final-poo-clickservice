package ar.edu.huergo.clickservice.buscadorservicios.dto;






import jakarta.validation.constraints.NotBlank;



/**
 * DTO para transferencia de datos de Producto
 * Representa los datos que se exponen en la API REST
 */
public record TareaDTO(
    
    Long id,
   
    @NotBlank(message = "El titulo es obligatorio")
    String titulo,

    String descripcion,

    String creador,

    boolean completada


) {
}
