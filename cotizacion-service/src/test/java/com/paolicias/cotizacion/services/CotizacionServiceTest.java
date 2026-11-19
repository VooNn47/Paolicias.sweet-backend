package com.paolicias.cotizacion.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import com.paolicias.cotizacion.client.CatalogoClient;
import com.paolicias.cotizacion.dto.Cotizacion;
import com.paolicias.cotizacion.dto.ProductoCatalogo;
import com.paolicias.cotizacion.entities.Tarifa;
import com.paolicias.cotizacion.repository.TarifaRepository;

// El catálogo (otro microservicio) y la tabla de tarifas se simulan con Mockito
class CotizacionServiceTest {

    private static final ProductoCatalogo TORTA = new ProductoCatalogo(1L, "Torta Frutos Rojos", 45000, 12);

    private CatalogoClient catalogo;
    private TarifaRepository tarifas;
    private CotizacionService service;

    @BeforeEach
    void setUp() {
        catalogo = mock(CatalogoClient.class);
        tarifas = mock(TarifaRepository.class);
        service = new CotizacionService(catalogo, tarifas);
    }

    // Simula la tabla con el tramo que contiene esa cantidad de invitados
    private void tarifa(int invitados, int min, int max, int monto) {
        when(tarifas.findFirstByMinInvitadosLessThanEqualAndMaxInvitadosGreaterThanEqual(invitados, invitados))
                .thenReturn(Optional.of(new Tarifa(1L, min, max, monto)));
    }

    @Test
    void tarifaServicio_usaElTramoDeLaTabla() {
        tarifa(25, 21, 30, 80000);

        assertEquals(80000, service.tarifaServicio(25));
    }

    @Test
    void tarifaServicio_sinTramo_esCero() {
        when(tarifas.findFirstByMinInvitadosLessThanEqualAndMaxInvitadosGreaterThanEqual(anyInt(), anyInt()))
                .thenReturn(Optional.empty());

        assertEquals(0, service.tarifaServicio(150));
    }

    // Ejemplo del README: 25 invitados + Torta Frutos Rojos = $215.000, abono $107.500
    @Test
    void calcular_ejemploDelReadme() {
        tarifa(25, 21, 30, 80000);
        when(catalogo.obtenerProducto(1L)).thenReturn(Optional.of(TORTA));

        Cotizacion c = service.calcular(25, List.of(1L));

        assertEquals(80000, c.servicio());
        assertEquals(3, c.lineas().get(0).unidades());
        assertEquals(135000, c.lineas().get(0).subtotal());
        assertEquals(215000, c.total());
        assertEquals(107500, c.abono());
    }

    @Test
    void calcular_ignoraProductosRepetidos() {
        tarifa(25, 21, 30, 80000);
        when(catalogo.obtenerProducto(1L)).thenReturn(Optional.of(TORTA));

        Cotizacion c = service.calcular(25, List.of(1L, 1L));

        assertEquals(1, c.lineas().size());
        verify(catalogo, times(1)).obtenerProducto(1L);
    }

    @Test
    void calcular_sinProductos_soloCobraElServicio() {
        tarifa(5, 1, 10, 30000);

        Cotizacion c = service.calcular(5, null);

        assertEquals(30000, c.total());
        assertEquals(15000, c.abono());
    }

    @Test
    void calcular_conProductoInexistente_lanza400() {
        tarifa(10, 1, 10, 30000);
        when(catalogo.obtenerProducto(99L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.calcular(10, List.of(99L)));

        assertEquals(400, ex.getStatusCode().value());
    }
}
