package com.paolicias.insumos.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.paolicias.insumos.dto.CalculoInsumos;
import com.paolicias.insumos.dto.CalculoRequest;
import com.paolicias.insumos.dto.LineaProducto;
import com.paolicias.insumos.dto.StockRequest;
import com.paolicias.insumos.entities.Insumo;
import com.paolicias.insumos.services.InsumoService;

// Pruebas unitarias del controller: el servicio se simula con Mockito.
class InsumoControllerTest {

    private InsumoService service;
    private InsumoController controller;

    @BeforeEach
    void setUp() {
        service = mock(InsumoService.class);
        controller = new InsumoController(service);
    }

    @Test
    void listarInsumos_devuelveElInventario() {
        when(service.listar()).thenReturn(List.of(new Insumo("Harina", "kg", 20.0)));

        assertEquals(1, controller.listarInsumos().size());
    }

    @Test
    void actualizarStock_usaElValorRecibido() {
        Insumo harina = new Insumo("Harina", "kg", 25.0);
        when(service.actualizarStock(1L, 25.0)).thenReturn(harina);

        assertEquals(25.0, controller.actualizarStock(1L, new StockRequest(25.0)).getStock());
    }

    @Test
    void calcular_devuelveSiAlcanza() {
        List<LineaProducto> lineas = List.of(new LineaProducto(1L, 9));
        when(service.calcular(lineas)).thenReturn(new CalculoInsumos(false, List.of(
                new CalculoInsumos.InsumoNecesario(2L, "Frutos rojos", "kg", 3.6, 3.0, 0.6))));

        CalculoInsumos c = controller.calcular(new CalculoRequest(lineas));

        assertFalse(c.alcanza());
        assertEquals(0.6, c.insumos().get(0).faltante());
    }
}
