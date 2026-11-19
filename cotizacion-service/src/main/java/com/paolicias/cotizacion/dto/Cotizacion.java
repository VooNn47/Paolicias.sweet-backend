package com.paolicias.cotizacion.dto;

import java.util.List;

// Resultado de la cotización (igual que LineaCotizacion.kt de la app)
public record Cotizacion(
        int invitados,
        int servicio,
        List<Linea> lineas,
        int total,
        int abono
) {
    public record Linea(Long productoId, String nombre, int unidades, int precioUnitario, int subtotal) {
    }
}
