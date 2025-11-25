package ar.edu.huergo.clickservice.buscadorservicios.service;





import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.huergo.clickservice.buscadorservicios.entity.Producto;
import ar.edu.huergo.clickservice.buscadorservicios.repository.ProductoRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class ProductoService {
    @Autowired
    private ProductoRepository productoRepository;

    public List<Producto> obtenerTodosLosProductos() {
        return productoRepository.findAll();
    }

    public Producto obtenerProductoPorId(Long id) throws EntityNotFoundException {
        return productoRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Ingrediente no encontrado"));
    }

    public Producto crearProducto(Producto producto) {
        return productoRepository.save(producto);
    }

    public Producto actualizarProducto(Long id, Producto producto) throws EntityNotFoundException {
        Producto productoExistente = obtenerProductoPorId(id);
       productoExistente.setNombre(producto.getNombre());
        productoExistente.setPrecio(producto.getPrecio());
        return productoRepository.save(productoExistente);
    }
    
    public void eliminarProducto(Long id) throws EntityNotFoundException {
         Producto producto = obtenerProductoPorId(id);
       productoRepository.delete(producto);
    }

    public List<Producto> obtenerProductoPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public List<Producto> obtenerProductoPorPrecio(int precio) {
        return productoRepository.findByPrecio(precio);
    }

    public List<Producto> resolverProductos(List<Long> productosIds) throws IllegalArgumentException, EntityNotFoundException {
        if (productosIds == null || productosIds.isEmpty()) {
            throw new IllegalArgumentException("Debe especificar al menos un ingrediente");
        }
        List<Producto> ingredientes = productoRepository.findAllById(productosIds);
        if (ingredientes.size() != productosIds.stream().filter(Objects::nonNull).distinct()
                .count()) {
            throw new EntityNotFoundException("Uno o más ingredientes no existen");
        }
        return ingredientes;
    }
}
