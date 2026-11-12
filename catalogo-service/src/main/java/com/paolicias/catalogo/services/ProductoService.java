package com.paolicias.catalogo.services;

import java.util.List;
import java.util.Optional;

import com.paolicias.catalogo.entities.Producto;

public interface ProductoService {

    List<Producto> obtenerTodos();

    Optional<Producto> obtenerPorId(Long id);

    Producto crear(Producto producto);
}
