package com.paolicias.cotizacion.dto;

import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

// Para mostrar el desglose al cliente ANTES de enviar la reserva
public record CotizacionRequest(
        @NotNull(message = "Ingresa la cantidad de invitados")
        @Min(value = 1, message = "Los invitados deben ser entre 1 y 100")
        @Max(value = 100, message = "Los invitados deben ser entre 1 y 100")
        Integer invitados,

        List<Long> productoIds
) {
}
