package com.paolicias.reservas.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.paolicias.reservas.entities.Reserva;
import com.paolicias.reservas.services.ReservaService;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    // GET /api/reservas  ->  todas las reservas
    @GetMapping
    public List<Reserva> listarReservas() {
        return reservaService.obtenerTodas();
    }

    // GET /api/reservas/{id}  ->  una reserva, o 404 si no existe
    @GetMapping("/{id}")
    public ResponseEntity<Reserva> obtenerPorId(@PathVariable Long id) {
        return reservaService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/reservas  ->  crea una reserva (201)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Reserva guardarReserva(@RequestBody Reserva reserva) {
        return reservaService.crear(reserva);
    }

}
