package com.paolicias.cotizacion.services;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.paolicias.cotizacion.client.CatalogoClient;
import com.paolicias.cotizacion.dto.Cotizacion;
import com.paolicias.cotizacion.dto.ProductoCatalogo;
import com.paolicias.cotizacion.entities.Tarifa;
import com.paolicias.cotizacion.repository.TarifaRepository;

// Regla de cotización del README de la app:
//   total = tarifa del servicio (según invitados) + Σ ceil(invitados / porciones) × precio
// La tarifa sale de la tabla "tarifas" y los precios se piden SIEMPRE a catalogo-service.
@Service
public class CotizacionService {

    private final CatalogoClient catalogo;
    private final TarifaRepository tarifaRepo;

    public CotizacionService(CatalogoClient catalogoClient, TarifaRepository tarifaRepository) {
        this.catalogo = catalogoClient;
        this.tarifaRepo = tarifaRepository;
    }

    // Tarifa del servicio para esa cantidad de invitados (0 si ningún tramo la contiene)
    public int tarifaServicio(int invitados) {
        return tarifaRepo.findFirstByMinInvitadosLessThanEqualAndMaxInvitadosGreaterThanEqual(invitados, invitados)
                .map(Tarifa::getMonto)
                .orElse(0);
    }

    public List<Tarifa> tarifas() {
        return tarifaRepo.findAllByOrderByMinInvitadosAsc();
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
