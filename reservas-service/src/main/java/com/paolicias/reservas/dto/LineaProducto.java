package com.paolicias.reservas.dto;

// Un producto de la reserva y cuántas unidades lleva (se envía a insumos-service)
public record LineaProducto(Long productoId, Integer unidades) {
}
