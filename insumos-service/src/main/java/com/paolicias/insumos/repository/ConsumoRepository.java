package com.paolicias.insumos.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.paolicias.insumos.entities.Consumo;

// La clave es el id de la reserva
public interface ConsumoRepository extends JpaRepository<Consumo, Long> {
}
