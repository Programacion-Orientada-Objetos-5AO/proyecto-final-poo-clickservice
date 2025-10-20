package ar.edu.huergo.clickservice.buscadorservicios.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import ar.edu.huergo.clickservice.buscadorservicios.entity.Comision;

@DataJpaTest
@DisplayName("Tests de Integración - ComisionRepository")
class ComisionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ComisionRepository comisionRepository;

    private Comision comision1;
    private Comision comision2;
    private Comision comision3;

    @BeforeEach
    void setUp() {
        comision1 = persistComision(100L, 12.0, 1000.0, 120.0, LocalDate.now().minusDays(2));
        comision2 = persistComision(101L, 10.0, 800.0, 80.0, LocalDate.now().minusDays(1));
        comision3 = persistComision(102L, 8.5, 600.0, 51.0, LocalDate.now());
        entityManager.clear();
    }

    private Comision persistComision(Long pagoId, Double tasa, Double base, Double monto, LocalDate fecha) {
        Comision comision = new Comision();
        comision.setPagoId(pagoId);
        comision.setTasa(tasa);
        comision.setBase(base);
        comision.setMonto(monto);
        comision.setFecha(fecha);
        return entityManager.persistAndFlush(comision);
    }

    @Test
    @DisplayName("Debería guardar y recuperar una comisión correctamente")
    void deberiaGuardarYRecuperarComision() {
        Comision nuevaComision = new Comision();
        nuevaComision.setPagoId(200L);
        nuevaComision.setTasa(9.0);
        nuevaComision.setBase(900.0);
        nuevaComision.setMonto(81.0);
        nuevaComision.setFecha(LocalDate.now());

        Comision comisionGuardada = comisionRepository.save(nuevaComision);
        entityManager.flush();
        entityManager.clear();

        assertNotNull(comisionGuardada.getId());

        Optional<Comision> comisionRecuperada =
                comisionRepository.findById(comisionGuardada.getId());

        assertTrue(comisionRecuperada.isPresent());
        assertEquals(200L, comisionRecuperada.get().getPagoId());
        assertEquals(9.0, comisionRecuperada.get().getTasa());
        assertEquals(900.0, comisionRecuperada.get().getBase());
        assertEquals(81.0, comisionRecuperada.get().getMonto());
    }

    @Test
    @DisplayName("Debería actualizar una comisión existente")
    void deberiaActualizarComisionExistente() {
        Optional<Comision> comisionOptional =
                comisionRepository.findById(comision1.getId());
        assertTrue(comisionOptional.isPresent());

        Comision comision = comisionOptional.get();
        comision.setTasa(14.0);
        comision.setMonto(140.0);
        comision.setFecha(LocalDate.now());

        Comision comisionActualizada = comisionRepository.save(comision);
        entityManager.flush();

        assertEquals(14.0, comisionActualizada.getTasa());
        assertEquals(140.0, comisionActualizada.getMonto());

        entityManager.clear();
        Optional<Comision> verificacion = comisionRepository.findById(comision1.getId());
        assertTrue(verificacion.isPresent());
        assertEquals(14.0, verificacion.get().getTasa());
        assertEquals(140.0, verificacion.get().getMonto());
    }

    @Test
    @DisplayName("Debería eliminar una comisión correctamente")
    void deberiaEliminarComision() {
        Long comisionId = comision2.getId();
        assertTrue(comisionRepository.existsById(comisionId));

        comisionRepository.deleteById(comisionId);
        entityManager.flush();

        assertFalse(comisionRepository.existsById(comisionId));
        assertTrue(comisionRepository.findById(comisionId).isEmpty());
    }

    @Test
    @DisplayName("Debería encontrar todas las comisiones")
    void deberiaEncontrarTodasLasComisiones() {
        List<Comision> todasLasComisiones = comisionRepository.findAll();

        assertNotNull(todasLasComisiones);
        assertEquals(3, todasLasComisiones.size());

        List<Long> pagos = todasLasComisiones.stream().map(Comision::getPagoId).toList();
        assertTrue(pagos.contains(100L));
        assertTrue(pagos.contains(101L));
        assertTrue(pagos.contains(102L));
    }

    @Test
    @DisplayName("Debería contar comisiones correctamente")
    void deberiaContarComisiones() {
        long cantidad = comisionRepository.count();
        assertEquals(3, cantidad);

        persistComision(103L, 7.5, 400.0, 30.0, LocalDate.now());
        assertEquals(4, comisionRepository.count());
    }

    @Test
    @DisplayName("Debería validar restricciones de tasa positiva")
    void deberiaValidarRestriccionTasaPositiva() {
        Comision comisionInvalida = new Comision();
        comisionInvalida.setPagoId(300L);
        comisionInvalida.setTasa(-5.0);
        comisionInvalida.setBase(500.0);
        comisionInvalida.setMonto(50.0);
        comisionInvalida.setFecha(LocalDate.now());

        assertThrows(Exception.class, () -> {
            entityManager.persistAndFlush(comisionInvalida);
        });
    }

    @Test
    @DisplayName("Debería validar restricción de pago obligatorio")
    void deberiaValidarRestriccionPagoObligatorio() {
        Comision comisionInvalida = new Comision();
        comisionInvalida.setPagoId(null);
        comisionInvalida.setTasa(10.0);
        comisionInvalida.setBase(500.0);
        comisionInvalida.setMonto(50.0);
        comisionInvalida.setFecha(LocalDate.now());

        assertThrows(Exception.class, () -> {
            entityManager.persistAndFlush(comisionInvalida);
        });
    }

    @Test
    @DisplayName("Debería encontrar comisión por ID específico")
    void deberiaEncontrarComisionPorIdEspecifico() {
        Optional<Comision> comisionEncontrada = comisionRepository.findById(comision3.getId());

        assertTrue(comisionEncontrada.isPresent());
        assertEquals(102L, comisionEncontrada.get().getPagoId());
        assertEquals(8.5, comisionEncontrada.get().getTasa());
    }

    @Test
    @DisplayName("Debería retornar Optional vacío para ID inexistente")
    void deberiaRetornarOptionalVacioParaIdInexistente() {
        Optional<Comision> comisionEncontrada = comisionRepository.findById(999L);
        assertTrue(comisionEncontrada.isEmpty());
    }

    @Test
    @DisplayName("Debería verificar existencia de comisión por ID")
    void deberiaVerificarExistenciaComisionPorId() {
        assertTrue(comisionRepository.existsById(comision1.getId()));
        assertTrue(comisionRepository.existsById(comision2.getId()));
        assertTrue(comisionRepository.existsById(comision3.getId()));
        assertFalse(comisionRepository.existsById(999L));
    }

    @Test
    @DisplayName("Debería eliminar comisión por entidad")
    void deberiaEliminarComisionPorEntidad() {
        Long comisionId = comision3.getId();
        assertTrue(comisionRepository.existsById(comisionId));

        comisionRepository.delete(comision3);
        entityManager.flush();

        assertFalse(comisionRepository.existsById(comisionId));
    }

    @Test
    @DisplayName("Debería manejar comisiones con montos decimales")
    void deberiaManejarComisionesConMontosDecimales() {
        Comision comisionDecimal = new Comision();
        comisionDecimal.setPagoId(400L);
        comisionDecimal.setTasa(7.25);
        comisionDecimal.setBase(750.0);
        comisionDecimal.setMonto(54.38);
        comisionDecimal.setFecha(LocalDate.now());

        Comision comisionGuardada = comisionRepository.save(comisionDecimal);
        entityManager.flush();
        entityManager.clear();

        Optional<Comision> comisionRecuperada =
                comisionRepository.findById(comisionGuardada.getId());

        assertTrue(comisionRecuperada.isPresent());
        assertEquals(54.38, comisionRecuperada.get().getMonto());
    }
}
