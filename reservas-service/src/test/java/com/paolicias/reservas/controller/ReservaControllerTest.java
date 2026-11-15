package com.paolicias.reservas.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import com.paolicias.reservas.entities.Reserva;
import com.paolicias.reservas.services.ReservaService;

// Pruebas unitarias del controller: el servicio se simula con Mockito.
class ReservaControllerTest {

    private ReservaService service;
    private ReservaController controller;

    @BeforeEach
    void setUp() {
        service = mock(ReservaService.class);
        controller = new ReservaController(service);
    }

    private Reserva reservaEjemplo(Long id) {
        Reserva r = new Reserva();
        r.setId(id);
        r.setNombreEvento("Cumpleanos");
        r.setFecha("2026-12-20");
        r.setHorario("20:00");
        r.setInvitados("30");
        return r;
    }

    @Test
    void listarReservas_devuelveTodasLasReservas() {
        when(service.obtenerTodas()).thenReturn(List.of(reservaEjemplo(1L), reservaEjemplo(2L)));

        List<Reserva> resultado = controller.listarReservas();

        assertEquals(2, resultado.size());
    }

    @Test
    void obtenerPorId_conIdExistente_responde200() {
        when(service.obtenerPorId(1L)).thenReturn(Optional.of(reservaEjemplo(1L)));

        ResponseEntity<Reserva> respuesta = controller.obtenerPorId(1L);

        assertEquals(200, respuesta.getStatusCode().value());
        assertEquals("Cumpleanos", respuesta.getBody().getNombreEvento());
    }

    @Test
    void obtenerPorId_conIdInexistente_responde404() {
        when(service.obtenerPorId(99L)).thenReturn(Optional.empty());

        ResponseEntity<Reserva> respuesta = controller.obtenerPorId(99L);

        assertEquals(404, respuesta.getStatusCode().value());
        assertNull(respuesta.getBody());
    }

    @Test
    void guardarReserva_devuelveLaReservaCreada() {
        Reserva nueva = reservaEjemplo(null);
        when(service.crear(nueva)).thenReturn(reservaEjemplo(7L));

        Reserva resultado = controller.guardarReserva(nueva);

        assertEquals(7L, resultado.getId());
    }
}
