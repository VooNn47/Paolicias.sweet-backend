package com.paolicias.insumos.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

// Nuevo stock de un insumo (la administradora lo ajusta cuando compra)
public record StockRequest(
        @NotNull(message = "Ingresa el stock")
        @PositiveOrZero(message = "El stock no puede ser negativo")
        Double stock
) {
}
