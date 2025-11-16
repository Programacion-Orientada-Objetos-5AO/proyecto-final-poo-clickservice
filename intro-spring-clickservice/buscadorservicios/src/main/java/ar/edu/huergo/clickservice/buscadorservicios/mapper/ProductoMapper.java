package ar.edu.huergo.clickservice.buscadorservicios.mapper;
 



import org.springframework.stereotype.Component;
import ar.edu.huergo.clickservice.buscadorservicios.dto.ProductoDTO;
import ar.edu.huergo.clickservice.buscadorservicios.entity.Producto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//Un Mapper es una clase que se encarga de convertir un objeto de un tipo a otro.
//En este caso, se encarga de convertir un objeto Plato a un objeto PlatoDTO y viceversa.
//Esto es útil para evitar que el controlador se encargue de la conversión de objetos.
@Component
public class ProductoMapper {

    /**
     * Convierte una entidad Plato a PlatoDTO
     */
    public ProductoDTO toDTO(Producto producto) {
        if (producto == null) {
            return null;
        }
        return new ProductoDTO(
            producto.getId(),
            producto.getNombre(),
            producto.getPrecio()
        );
    }

    /**
     * Convierte un PlatoDTO a entidad Plato
     */
    public Producto toEntity(ProductoDTO dto) {
        if (dto == null) {
            return null;
        }
        Producto producto = new Producto();
        producto.setNombre(dto.nombre());
        producto.setPrecio(dto.precio());
        return producto;

        

        
    }

    /**
     * Convierte una lista de entidades Plato a lista de PlatoDTO
     */
    public List<ProductoDTO> toDTOList(List<Producto> productos) {
        if (productos == null) {
            return new ArrayList<>();
        }
        return productos.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}