package ar.edu.huergo.clickservice.buscadorservicios.dto.security;

import java.util.List;

public record UsuarioDTO(String username, List<String> roles) {
    
}
