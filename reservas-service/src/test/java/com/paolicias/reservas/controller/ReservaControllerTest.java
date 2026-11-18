package com.paolicias.reservas.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.paolicias.reservas.entities.EstadoAbono;
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

    private Reserva reservaEjemplo(Long id, String estado) {
        Reserva r = new Reserva();
        r.setId(id);
        r.setNombreEvento("Cumpleanos");
        r.setEstadoAbono(estado);
        r.setTotal(64000);
        return r;
    }

    @Test
    void listarReservas_pasaLosFiltrosAlServicio() {
        when(service.listar("ana@correo.cl", null)).thenReturn(List.of(reservaEjemplo(1L, EstadoAbono.PENDIENTE)));

        assertEquals(1, controller.listarReservas("ana@correo.cl", null).size());
    }

    @Test
    void obtenerPorId_conIdExistente_devuelveLaReserva() {
        when(service.obtener(1L)).thenReturn(reservaEjemplo(1L, EstadoAbono.PENDIENTE));

        Reserva r = controller.obtenerPorId(1L);

        assertEquals("Cumpleanos", r.getNombreEvento());
        assertEquals(32000, r.getAbono());
    }

    @Test
    void obtenerPorId_conIdInexistente_propagaEl404() {
        when(service.obtener(99L)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        assertThrows(ResponseStatusException.class, () -> controller.obtenerPorId(99L));
    }

    @Test
    void validar_devuelveLaReservaPagada() {
        when(service.validarAbono(1L)).thenReturn(reservaEjemplo(1L, EstadoAbono.PAGADO));

        assertEquals(EstadoAbono.PAGADO, controller.validar(1L).getEstadoAbono());
    }
}
