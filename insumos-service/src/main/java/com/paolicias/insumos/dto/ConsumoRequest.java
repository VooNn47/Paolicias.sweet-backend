package com.paolicias.insumos.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

// Para descontar del stock los insumos de una reserva (cuando se valida su abono)
public record ConsumoRequest(
        @NotNull(message = "Falta la reserva")
        Long reservaId,

        List<@Valid LineaProducto> lineas
) {
}
