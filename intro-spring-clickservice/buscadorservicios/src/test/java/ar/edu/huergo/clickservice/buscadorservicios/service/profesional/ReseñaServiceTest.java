package ar.edu.huergo.clickservice.buscadorservicios.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ar.edu.huergo.clickservice.buscadorservicios.entity.Profesional;
import ar.edu.huergo.clickservice.buscadorservicios.entity.Reseña;
import ar.edu.huergo.clickservice.buscadorservicios.entity.security.Usuario;
import ar.edu.huergo.clickservice.buscadorservicios.repository.ProfesionalRepository;
import ar.edu.huergo.clickservice.buscadorservicios.repository.ReseñaRepository;
import ar.edu.huergo.clickservice.buscadorservicios.repository.security.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de Unidad - ReseñaService")
class ReseñaServiceTest {

    @Mock
    private ReseñaRepository reseñaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ProfesionalRepository profesionalRepository;

    @InjectMocks
    private ReseñaService reseñaService;

    private Reseña reseña;
    private Usuario usuario;
    private Profesional profesional;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("cliente@example.com");
        usuario.setPassword("contraseñaSegura123");

        profesional = new Profesional();
        profesional.setId(2L);
        profesional.setUsuario(new Usuario());
        profesional.setNombreCompleto("Profesional Demo");

        reseña = new Reseña();
        reseña.setId(3L);
        reseña.setOrdenId(100L);
        reseña.setRating(5);
        reseña.setComentario("Excelente trabajo y atención");
        reseña.setFecha(LocalDateTime.of(2024, 1, 1, 10, 0));
        reseña.setUsuario(usuario);
        reseña.setProfesional(profesional);
    }

    @Test
    @DisplayName("Debería obtener todas las reseñas")
    void deberiaObtenerTodasLasReseñas() {
        when(reseñaRepository.findAll()).thenReturn(Arrays.asList(reseña));

        List<Reseña> resultado = reseñaService.obtenerTodasLasReseñas();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(reseñaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería obtener reseña por ID cuando existe")
    void deberiaObtenerReseñaPorIdCuandoExiste() {
        when(reseñaRepository.findById(reseña.getId())).thenReturn(Optional.of(reseña));

        Reseña resultado = reseñaService.obtenerReseñaPorId(reseña.getId());

        assertNotNull(resultado);
        assertEquals(reseña.getComentario(), resultado.getComentario());
        verify(reseñaRepository, times(1)).findById(reseña.getId());
    }

    @Test
    @DisplayName("Debería lanzar EntityNotFoundException cuando la reseña no existe")
    void deberiaLanzarExcepcionCuandoReseñaNoExiste() {
        when(reseñaRepository.findById(anyLong())).thenReturn(Optional.empty());

        EntityNotFoundException excepcion = assertThrows(EntityNotFoundException.class,
                () -> reseñaService.obtenerReseñaPorId(999L));

        assertEquals("Reseña no encontrada", excepcion.getMessage());
        verify(reseñaRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Debería obtener reseñas por profesional")
    void deberiaObtenerReseñasPorProfesional() {
        when(reseñaRepository.findByProfesionalId(profesional.getId()))
                .thenReturn(Collections.singletonList(reseña));

        List<Reseña> resultado = reseñaService.obtenerReseñasPorProfesional(profesional.getId());

        assertEquals(1, resultado.size());
        verify(reseñaRepository, times(1)).findByProfesionalId(profesional.getId());
    }

    @Test
    @DisplayName("Debería obtener reseñas por usuario")
    void deberiaObtenerReseñasPorUsuario() {
        when(reseñaRepository.findByUsuarioId(usuario.getId()))
                .thenReturn(Collections.singletonList(reseña));

        List<Reseña> resultado = reseñaService.obtenerReseñasPorUsuario(usuario.getId());

        assertEquals(1, resultado.size());
        verify(reseñaRepository, times(1)).findByUsuarioId(usuario.getId());
    }

    @Test
    @DisplayName("Debería obtener reseña por orden cuando existe")
    void deberiaObtenerReseñaPorOrdenCuandoExiste() {
        when(reseñaRepository.findByOrdenId(reseña.getOrdenId())).thenReturn(Optional.of(reseña));

        Reseña resultado = reseñaService.obtenerReseñaPorOrden(reseña.getOrdenId());

        assertNotNull(resultado);
        assertEquals(reseña.getOrdenId(), resultado.getOrdenId());
        verify(reseñaRepository, times(1)).findByOrdenId(reseña.getOrdenId());
    }

    @Test
    @DisplayName("Debería lanzar EntityNotFoundException cuando la reseña por orden no existe")
    void deberiaLanzarExcepcionCuandoReseñaPorOrdenNoExiste() {
        when(reseñaRepository.findByOrdenId(anyLong())).thenReturn(Optional.empty());

        EntityNotFoundException excepcion = assertThrows(EntityNotFoundException.class,
                () -> reseñaService.obtenerReseñaPorOrden(555L));

        assertEquals("Reseña no encontrada para la orden especificada", excepcion.getMessage());
        verify(reseñaRepository, times(1)).findByOrdenId(555L);
    }

    @Test
    @DisplayName("Debería crear reseña asignando usuario, profesional y fecha")
    void deberiaCrearReseñaAsignandoUsuarioProfesionalYFecha() {
        Reseña reseñaNueva = new Reseña();
        reseñaNueva.setOrdenId(200L);
        reseñaNueva.setRating(4);
        reseñaNueva.setComentario("Muy buen servicio");
        reseñaNueva.setFecha(null);

        when(reseñaRepository.existsByOrdenId(reseñaNueva.getOrdenId())).thenReturn(false);
        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
        when(profesionalRepository.findById(profesional.getId())).thenReturn(Optional.of(profesional));
        when(reseñaRepository.save(any(Reseña.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Reseña resultado = reseñaService.crearReseña(reseñaNueva, usuario.getId(), profesional.getId());

        assertNotNull(resultado);
        assertEquals(usuario, resultado.getUsuario());
        assertEquals(profesional, resultado.getProfesional());
        assertNotNull(resultado.getFecha());
        verify(reseñaRepository, times(1)).save(any(Reseña.class));
    }

    @Test
    @DisplayName("Debería lanzar excepción al crear reseña duplicada")
    void deberiaLanzarExcepcionAlCrearReseñaDuplicada() {
        Reseña reseñaDuplicada = new Reseña();
        reseñaDuplicada.setOrdenId(300L);

        when(reseñaRepository.existsByOrdenId(reseñaDuplicada.getOrdenId())).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> reseñaService.crearReseña(reseñaDuplicada, usuario.getId(), profesional.getId()));

        verify(reseñaRepository, never()).save(any());
        verify(usuarioRepository, never()).findById(anyLong());
        verify(profesionalRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("Debería actualizar reseña existente")
    void deberiaActualizarReseñaExistente() {
        Reseña reseñaExistente = new Reseña();
        reseñaExistente.setId(reseña.getId());
        reseñaExistente.setOrdenId(reseña.getOrdenId());
        reseñaExistente.setRating(5);
        reseñaExistente.setComentario("Comentario original");
        reseñaExistente.setFecha(LocalDateTime.of(2023, 12, 1, 9, 0));
        reseñaExistente.setUsuario(usuario);
        reseñaExistente.setProfesional(profesional);

        when(reseñaRepository.findById(reseña.getId())).thenReturn(Optional.of(reseñaExistente));
        when(reseñaRepository.save(any(Reseña.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Reseña cambios = new Reseña();
        cambios.setRating(3);
        cambios.setComentario("Se puede mejorar en la puntualidad");

        Reseña resultado = reseñaService.actualizarReseña(reseña.getId(), cambios);

        assertEquals(3, resultado.getRating());
        assertEquals("Se puede mejorar en la puntualidad", resultado.getComentario());
        assertNotEquals(LocalDateTime.of(2023, 12, 1, 9, 0), resultado.getFecha());
        verify(reseñaRepository, times(1)).save(reseñaExistente);
    }

    @Test
    @DisplayName("Debería lanzar EntityNotFoundException al actualizar reseña inexistente")
    void deberiaLanzarExcepcionAlActualizarReseñaInexistente() {
        when(reseñaRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> reseñaService.actualizarReseña(404L, reseña));

        verify(reseñaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería eliminar reseña existente")
    void deberiaEliminarReseñaExistente() {
        when(reseñaRepository.findById(reseña.getId())).thenReturn(Optional.of(reseña));

        assertDoesNotThrow(() -> reseñaService.eliminarReseña(reseña.getId()));

        verify(reseñaRepository, times(1)).delete(reseña);
    }

    @Test
    @DisplayName("Debería lanzar EntityNotFoundException al eliminar reseña inexistente")
    void deberiaLanzarExcepcionAlEliminarReseñaInexistente() {
        when(reseñaRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> reseñaService.eliminarReseña(123L));

        verify(reseñaRepository, never()).delete(any());
    }
}
