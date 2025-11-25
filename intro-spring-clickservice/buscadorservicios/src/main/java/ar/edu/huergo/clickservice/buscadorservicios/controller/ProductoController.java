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

import ar.edu.huergo.clickservice.buscadorservicios.service.ProductoService;
import jakarta.validation.Valid;
import ar.edu.huergo.clickservice.buscadorservicios.dto.ProductoDTO;
import ar.edu.huergo.clickservice.buscadorservicios.mapper.ProductoMapper;

@RestController
@RequestMapping("/api/productos")

public class ProductoController {

    @Autowired
    private ProductoService productoService;

    
    @Autowired
    private ProductoMapper productoMapper;

    @GetMapping
    public ResponseEntity<List<ProductoDTO>> obtenerTodosLosProductos() {
        return ResponseEntity.ok(productoMapper.toDTOList(productoService.obtenerTodosLosProductos()));
    }

    @PostMapping
    public ResponseEntity<ProductoDTO> crearProducto(@RequestBody @Valid ProductoDTO productoDTO) {
        ProductoDTO productoCreado = productoMapper.toDTO(productoService.crearProducto(productoMapper.toEntity(productoDTO)));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(productoCreado.id())
            .toUri();
        return ResponseEntity.created(location).body(productoCreado);
    }

     @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> obtenerProductoPorId(@PathVariable Long id) {
        return ResponseEntity.ok(productoMapper.toDTO(productoService.obtenerProductoPorId(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoDTO> actualizarProducto(@PathVariable Long id, @RequestBody @Valid ProductoDTO productoDTO) {
        return ResponseEntity.ok(productoMapper.toDTO( productoService.actualizarProducto(  id,productoMapper.toEntity(productoDTO))) );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build(); }
    
    @GetMapping("/nombre")
    public ResponseEntity<List<ProductoDTO>> obtenerProductosPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(productoMapper.toDTOList(productoService.obtenerProductoPorNombre(nombre)));
    }

    @GetMapping("/precio")
    public ResponseEntity<List<ProductoDTO>> obtenerProductosPorPrecio(@RequestParam int precio) {
        return ResponseEntity.ok(productoMapper.toDTOList(productoService.obtenerProductoPorPrecio(precio)));
    }
    
    
    }
    











    

