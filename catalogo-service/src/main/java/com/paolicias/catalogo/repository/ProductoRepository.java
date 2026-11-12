package com.paolicias.catalogo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.paolicias.catalogo.entities.Producto;

// JpaRepository ya trae findAll, findById, save, count, etc.
public interface ProductoRepository extends JpaRepository<Producto, Long> {

}
