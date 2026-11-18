package com.paolicias.catalogo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.paolicias.catalogo.entities.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Mismo orden que el catálogo de la app
    List<Producto> findAllByOrderByCategoriaAscNombreAsc();
}
