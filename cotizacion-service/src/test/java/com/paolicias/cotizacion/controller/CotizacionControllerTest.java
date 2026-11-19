package com.paolicias.cotizacion.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.paolicias.cotizacion.dto.Cotizacion;
import com.paolicias.cotizacion.dto.CotizacionRequest;
import com.paolicias.cotizacion.entities.Tarifa;
import com.paolicias.cotizacion.services.CotizacionService;

// Pruebas unitarias de los controllers: el servicio se simula con Mockito.
class CotizacionControllerTest {

    private CotizacionService service;

    @BeforeEach
    void setUp() {
        service = mock(CotizacionService.class);
    }

    @Test
    void cotizar_devuelveElDesgloseDelServicio() {
        when(service.calcular(25, List.of(1L))).thenReturn(new Cotizacion(25, 80000,
                List.of(new Cotizacion.Linea(1L, "Torta", 3, 45000, 135000)), 215000, 107500));

        Cotizacion c = new CotizacionController(service).cotizar(new CotizacionRequest(25, List.of(1L)));

        assertEquals(215000, c.total());
        assertEquals(107500, c.abono());
    }

    @Test
    void listarTarifas_devuelveLosTramos() {
        when(service.tarifas()).thenReturn(List.of(
                new Tarifa(1L, 1, 10, 30000),
                new Tarifa(2L, 11, 20, 50000)));

        List<Tarifa> tarifas = new TarifaController(service).listarTarifas();

        assertEquals(2, tarifas.size());
        assertEquals(30000, tarifas.get(0).getMonto());
    }
}
