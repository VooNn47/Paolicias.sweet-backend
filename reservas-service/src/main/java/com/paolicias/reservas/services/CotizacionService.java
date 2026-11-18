package com.paolicias.reservas.services;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.paolicias.reservas.client.CatalogoClient;
import com.paolicias.reservas.dto.Cotizacion;
import com.paolicias.reservas.dto.ProductoCatalogo;

// Regla de cotización del README de la app:
//   total = tarifa del servicio (según invitados) + Σ ceil(invitados / porciones) × precio
// Los precios se piden SIEMPRE a catalogo-service, nunca se confía en lo que mande la app.
@Service
public class CotizacionService {

    // 1-10 -> 30.000, 11-20 -> 50.000, ... 91-100 -> 220.000
    private static final int[] TARIFAS = {
            30000, 50000, 80000, 100000, 120000,
            140000, 160000, 180000, 200000, 220000
    };

    private final CatalogoClient catalogo;

    public CotizacionService(CatalogoClient catalogoClient) {
        this.catalogo = catalogoClient;
    }

    public static int tarifaServicio(int invitados) {
        if (invitados < 1 || invitados > 100) {
            return 0;
        }
        return TARIFAS[(invitados - 1) / 10];
    }

    public Cotizacion calcular(int invitados, List<Long> productoIds) {
        // Sin repetidos y en el mismo orden en que llegaron
        List<Long> ids = productoIds == null ? List.of() : new ArrayList<>(new LinkedHashSet<>(productoIds));

        List<Cotizacion.Linea> lineas = new ArrayList<>();
        for (Long id : ids) {
            ProductoCatalogo p = catalogo.obtenerProducto(id)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "El producto " + id + " no existe"));
            int porciones = Math.max(1, p.porciones());
            int unidades = (invitados + porciones - 1) / porciones; // división hacia arriba
            lineas.add(new Cotizacion.Linea(p.id(), p.nombre(), unidades, p.precio(), unidades * p.precio()));
        }

        int servicio = tarifaServicio(invitados);
        int total = servicio + lineas.stream().mapToInt(Cotizacion.Linea::subtotal).sum();
        return new Cotizacion(invitados, servicio, lineas, total, total / 2);
    }
}
