package ar.edu.huergo.clickservice.buscadorservicios.service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import ar.edu.huergo.clickservice.buscadorservicios.entity.SolicitudServicio;
import ar.edu.huergo.clickservice.buscadorservicios.entity.SolicitudServicio.EstadoSolicitud;
import ar.edu.huergo.clickservice.buscadorservicios.repository.SolicitudServicioRepository;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de Unidad - SolicitudServicioService")
class SolicitudServicioServiceTest {

    @Mock
    private SolicitudServicioRepository solicitudServicioRepository;

    @InjectMocks
    private SolicitudServicioService solicitudServicioService;

    private SolicitudServicio crearSolicitudDemo(Long id) {
        SolicitudServicio s = new SolicitudServicio();
        s.setId(id);
        s.setDescripcionProblema("Problema en la instalación eléctrica");
        s.setDireccionServicio("Calle Falsa 123");
        s.setFechaSolicitada(LocalDate.of(2025, 10, 1));
        s.setFranjaHoraria("Tarde");
        s.setPresupuestoMaximo(5000.0);
        s.setComentariosAdicionales("Traer herramientas");
        s.setEstado(EstadoSolicitud.PENDIENTE);
        return s;
    }

    @Test
    @DisplayName("obtenerTodasLasSolicitudes: retorna lista completa")
    void obtenerTodasLasSolicitudes_ok() {
        when(solicitudServicioRepository.findAll()).thenReturn(Arrays.asList(crearSolicitudDemo(1L), crearSolicitudDemo(2L)));

        List<SolicitudServicio> result = solicitudServicioService.obtenerTodasLasSolicitudes();

        assertEquals(2, result.size());
        verify(solicitudServicioRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("obtenerSolicitudPorId: cuando existe, retorna entidad")
    void obtenerSolicitudPorId_existente() {
        SolicitudServicio demo = crearSolicitudDemo(1L);
        when(solicitudServicioRepository.findById(1L)).thenReturn(Optional.of(demo));

        SolicitudServicio result = solicitudServicioService.obtenerSolicitudPorId(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(solicitudServicioRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("obtenerSolicitudPorId: cuando no existe, lanza EntityNotFoundException")
    void obtenerSolicitudPorId_inexistente() {
        when(solicitudServicioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> solicitudServicioService.obtenerSolicitudPorId(99L));
        verify(solicitudServicioRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("crearSolicitud: guarda y retorna entidad creada")
    void crearSolicitud_ok() {
        SolicitudServicio demo = crearSolicitudDemo(null);
        SolicitudServicio guardado = crearSolicitudDemo(1L);
        when(solicitudServicioRepository.save(any(SolicitudServicio.class))).thenReturn(guardado);

        SolicitudServicio result = solicitudServicioService.crearSolicitud(demo);

        assertNotNull(result.getId());
        assertEquals("Problema en la instalación eléctrica", result.getDescripcionProblema());
        verify(solicitudServicioRepository, times(1)).save(any(SolicitudServicio.class));
    }

    @Test
    @DisplayName("actualizarSolicitud: cuando existe, actualiza campos")
    void actualizarSolicitud_existente() {
        SolicitudServicio existente = crearSolicitudDemo(1L);
        SolicitudServicio cambios = crearSolicitudDemo(1L);
        cambios.setDescripcionProblema("Problema actualizado");

        when(solicitudServicioRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(solicitudServicioRepository.save(any(SolicitudServicio.class))).thenReturn(cambios);

        SolicitudServicio result = solicitudServicioService.actualizarSolicitud(1L, cambios);

        assertEquals("Problema actualizado", result.getDescripcionProblema());
        verify(solicitudServicioRepository).findById(1L);
        verify(solicitudServicioRepository).save(any(SolicitudServicio.class));
    }

    @Test
    @DisplayName("actualizarSolicitud: cuando no existe, lanza EntityNotFoundException")
    void actualizarSolicitud_inexistente() {
        SolicitudServicio cambios = crearSolicitudDemo(1L);
        when(solicitudServicioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> solicitudServicioService.actualizarSolicitud(1L, cambios));
        verify(solicitudServicioRepository).findById(1L);
    }
}
