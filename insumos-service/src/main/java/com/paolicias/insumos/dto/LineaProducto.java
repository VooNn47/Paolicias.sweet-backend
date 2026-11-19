package com.paolicias.insumos.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

// Un producto de la reserva y cuántas unidades lleva (ej: 3 tortas)
public record LineaProducto(
        @NotNull(message = "Falta el producto")
        Long productoId,

        @NotNull(message = "Faltan las unidades")
        @Min(value = 1, message = "Las unidades deben ser al menos 1")
        Integer unidades
) {
}
