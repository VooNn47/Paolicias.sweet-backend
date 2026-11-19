package com.paolicias.insumos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.paolicias.insumos.entities.Receta;

public interface RecetaRepository extends JpaRepository<Receta, Long> {

    // Insumos que lleva una unidad del producto
    List<Receta> findByProductoId(Long productoId);
}
