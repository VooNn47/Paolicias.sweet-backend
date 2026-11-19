package com.paolicias.insumos.dto;

import java.util.List;

import jakarta.validation.Valid;

// Para calcular (sin descontar) los insumos de una lista de productos
public record CalculoRequest(
        List<@Valid LineaProducto> lineas
) {
}
