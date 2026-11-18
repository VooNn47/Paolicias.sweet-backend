package com.paolicias.reservas.dto;

// Los datos de un producto que reservas-service necesita de catalogo-service
public record ProductoCatalogo(Long id, String nombre, Integer precio, Integer porciones) {
}
