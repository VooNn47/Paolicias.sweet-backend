package com.paolicias.reservas.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.paolicias.reservas.entities.Reserva;
import com.paolicias.reservas.repository.ReservaRepository;

@Service
public class ReservaServiceImpl implements ReservaService {

    private final ReservaRepository reservaRepo;

    public ReservaServiceImpl(ReservaRepository reservaRepository) {
        this.reservaRepo = reservaRepository;
    }

    @Override
    public List<Reserva> obtenerTodas() {
        return reservaRepo.findAll();
    }

    @Override
    public Optional<Reserva> obtenerPorId(Long id) {
        return reservaRepo.findById(id);
    }

    @Override
    public Reserva crear(Reserva reserva) {
        return reservaRepo.save(reserva);
    }

}
