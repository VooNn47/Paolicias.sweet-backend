package com.paolicias.reservas.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import com.paolicias.reservas.client.CatalogoClient;
import com.paolicias.reservas.dto.Cotizacion;
import com.paolicias.reservas.dto.ProductoCatalogo;

// El catálogo (otro microservicio) se simula con Mockito
class CotizacionServiceTest {

    private CatalogoClient catalogo;
    private CotizacionService service;

    @BeforeEach
    void setUp() {
        catalogo = mock(CatalogoClient.class);
        service = new CotizacionService(catalogo);
    }

    private static final ProductoCatalogo TORTA = new ProductoCatalogo(1L, "Torta Frutos Rojos", 45000, 12);

    @Test
    void tarifaServicio_porTramosDe10() {
        assertEquals(30000, CotizacionService.tarifaServicio(1));
        assertEquals(30000, CotizacionService.tarifaServicio(10));
        assertEquals(50000, CotizacionService.tarifaServicio(11));
        assertEquals(80000, CotizacionService.tarifaServicio(25));
        assertEquals(220000, CotizacionService.tarifaServicio(100));
        assertEquals(0, CotizacionService.tarifaServicio(101));
    }

    // Ejemplo del README: 25 invitados + Torta Frutos Rojos = $215.000, abono $107.500
    @Test
    void calcular_ejemploDelReadme() {
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
        when(catalogo.obtenerProducto(1L)).thenReturn(Optional.of(TORTA));

        Cotizacion c = service.calcular(25, List.of(1L, 1L));

        assertEquals(1, c.lineas().size());
        verify(catalogo, times(1)).obtenerProducto(1L);
    }

    @Test
    void calcular_sinProductos_soloCobraElServicio() {
        Cotizacion c = service.calcular(5, null);

        assertEquals(30000, c.total());
        assertEquals(15000, c.abono());
    }

    @Test
    void calcular_conProductoInexistente_lanza400() {
        when(catalogo.obtenerProducto(99L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.calcular(10, List.of(99L)));

        assertEquals(400, ex.getStatusCode().value());
    }
}
