package com.paolicias.reservas.services;

import java.util.List;
import java.util.Optional;

import com.paolicias.reservas.entities.Reserva;

public interface ReservaService {

    List<Reserva> obtenerTodas();

    Optional<Reserva> obtenerPorId(Long id);

    Reserva crear(Reserva reserva);
}
