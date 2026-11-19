package com.paolicias.reservas.dto;

import java.util.List;

// Respuesta de insumos-service: si alcanza el inventario y el detalle por insumo
public record CalculoInsumos(
        boolean alcanza,
        List<InsumoNecesario> insumos
) {
    public record InsumoNecesario(
            Long insumoId,
            String nombre,
            String unidad,
            double necesario,
            double stock,
            double faltante
    ) {
    }
}
