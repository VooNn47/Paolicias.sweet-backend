package com.paolicias.insumos.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import com.paolicias.insumos.dto.CalculoInsumos;
import com.paolicias.insumos.dto.LineaProducto;
import com.paolicias.insumos.entities.Consumo;
import com.paolicias.insumos.entities.Insumo;
import com.paolicias.insumos.entities.Receta;
import com.paolicias.insumos.repository.ConsumoRepository;
import com.paolicias.insumos.repository.InsumoRepository;
import com.paolicias.insumos.repository.RecetaRepository;

// Pruebas unitarias: los repositorios se simulan con Mockito (no usan la base de datos).
class InsumoServiceTest {

    private InsumoRepository insumos;
    private RecetaRepository recetas;
    private ConsumoRepository consumos;
    private InsumoService service;

    private Insumo harina;
    private Insumo frutosRojos;

    @BeforeEach
    void setUp() {
        insumos = mock(InsumoRepository.class);
        recetas = mock(RecetaRepository.class);
        consumos = mock(ConsumoRepository.class);
        service = new InsumoService(insumos, recetas, consumos);

        harina = new Insumo("Harina", "kg", 20.0);
        harina.setId(1L);
        frutosRojos = new Insumo("Frutos rojos", "kg", 3.0);
        frutosRojos.setId(2L);

        // 1 Torta Frutos Rojos = 0,5 kg harina + 0,4 kg frutos rojos
        when(recetas.findByProductoId(1L)).thenReturn(List.of(
                new Receta(1L, harina, 0.5),
                new Receta(1L, frutosRojos, 0.4)));
        when(insumos.findById(1L)).thenReturn(Optional.of(harina));
        when(insumos.findById(2L)).thenReturn(Optional.of(frutosRojos));
        when(insumos.save(any(Insumo.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void calcular_multiplicaLaRecetaPorLasUnidades() {
        // 3 tortas -> 1,5 kg harina y 1,2 kg frutos rojos
        CalculoInsumos c = service.calcular(List.of(new LineaProducto(1L, 3)));

        assertTrue(c.alcanza());
        assertEquals(2, c.insumos().size());
        CalculoInsumos.InsumoNecesario fr = c.insumos().stream()
                .filter(i -> i.nombre().equals("Frutos rojos")).findFirst().orElseThrow();
        assertEquals(1.2, fr.necesario());
        assertEquals(0.0, fr.faltante());
    }

    @Test
    void calcular_detectaLoQueFaltaYLoMuestraPrimero() {
        // 9 tortas -> 3,6 kg de frutos rojos, pero hay 3 kg: faltan 0,6 kg
        CalculoInsumos c = service.calcular(List.of(new LineaProducto(1L, 9)));

        assertFalse(c.alcanza());
        assertEquals("Frutos rojos", c.insumos().get(0).nombre());
        assertEquals(0.6, c.insumos().get(0).faltante());
    }

    @Test
    void calcular_sinProductos_alcanza() {
        CalculoInsumos c = service.calcular(List.of());

        assertTrue(c.alcanza());
        assertTrue(c.insumos().isEmpty());
    }

    @Test
    void consumir_descuentaElStockYRegistraElConsumo() {
        when(consumos.existsById(10L)).thenReturn(false);

        service.consumir(10L, List.of(new LineaProducto(1L, 3)));

        assertEquals(18.5, harina.getStock());
        assertEquals(1.8, frutosRojos.getStock());
        verify(consumos).save(any(Consumo.class));
    }

    @Test
    void consumir_siFaltaAlgo_lanza409YNoDescuentaNada() {
        when(consumos.existsById(10L)).thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.consumir(10L, List.of(new LineaProducto(1L, 9))));

        assertEquals(409, ex.getStatusCode().value());
        assertTrue(ex.getReason().contains("Frutos rojos"));
        assertEquals(20.0, harina.getStock());
        verify(consumos, never()).save(any());
    }

    @Test
    void consumir_unaReservaYaDescontada_noDescuentaDosVeces() {
        when(consumos.existsById(10L)).thenReturn(true);

        service.consumir(10L, List.of(new LineaProducto(1L, 3)));

        assertEquals(20.0, harina.getStock());
        verify(insumos, never()).save(any());
    }

    @Test
    void actualizarStock_cambiaElValor() {
        Insumo actualizado = service.actualizarStock(2L, 5.0);

        assertEquals(5.0, actualizado.getStock());
    }

    @Test
    void actualizarStock_insumoInexistente_lanza404() {
        when(insumos.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.actualizarStock(99L, 1.0));

        assertEquals(404, ex.getStatusCode().value());
    }
}
