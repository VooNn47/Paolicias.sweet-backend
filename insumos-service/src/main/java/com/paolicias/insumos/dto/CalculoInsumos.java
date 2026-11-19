package com.paolicias.insumos.dto;

import java.util.List;

// Resultado del cálculo: si alcanza el stock y el detalle por insumo
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
