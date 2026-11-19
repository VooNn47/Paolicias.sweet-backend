package com.paolicias.insumos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.paolicias.insumos.entities.Insumo;

public interface InsumoRepository extends JpaRepository<Insumo, Long> {

    List<Insumo> findAllByOrderByNombreAsc();

    Optional<Insumo> findByNombre(String nombre);
}
