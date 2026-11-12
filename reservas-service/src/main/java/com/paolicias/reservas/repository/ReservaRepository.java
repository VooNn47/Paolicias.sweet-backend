package com.paolicias.reservas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.paolicias.reservas.entities.Reserva;

// JpaRepository ya trae findAll, findById, save, count, etc.
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

}
