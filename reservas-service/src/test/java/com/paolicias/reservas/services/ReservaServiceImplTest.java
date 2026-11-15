package com.paolicias.reservas.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.paolicias.reservas.entities.Reserva;
import com.paolicias.reservas.repository.ReservaRepository;

// Pruebas unitarias del servicio: el repositorio se simula con Mockito (no usa la base de datos).
class ReservaServiceImplTest {

    private ReservaRepository repo;
    private ReservaServiceImpl service;

    @BeforeEach
    void setUp() {
        repo = mock(ReservaRepository.class);
        service = new ReservaServiceImpl(repo);
    }

    private Reserva reservaEjemplo(Long id, String nombreEvento) {
        Reserva r = new Reserva();
        r.setId(id);
        r.setNombreEvento(nombreEvento);
        r.setFecha("2026-12-20");
        r.setHorario("20:00");
        r.setInvitados("30");
        r.setEstadoAbono("Pendiente");
        r.setTotal(64000);
        return r;
    }

    @Test
    void obtenerTodas_devuelveLaListaDelRepositorio() {
        when(repo.findAll()).thenReturn(List.of(
                reservaEjemplo(1L, "Cumpleanos"),
                reservaEjemplo(2L, "Matrimonio")));

        List<Reserva> resultado = service.obtenerTodas();

        assertEquals(2, resultado.size());
        assertEquals("Cumpleanos", resultado.get(0).getNombreEvento());
    }

    @Test
    void obtenerPorId_conIdExistente_devuelveLaReserva() {
        when(repo.findById(1L)).thenReturn(Optional.of(reservaEjemplo(1L, "Cumpleanos")));

        Optional<Reserva> resultado = service.obtenerPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals("Cumpleanos", resultado.get().getNombreEvento());
    }

    @Test
    void obtenerPorId_conIdInexistente_devuelveVacio() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertFalse(service.obtenerPorId(99L).isPresent());
    }

    @Test
    void crear_guardaLaReservaYLaDevuelve() {
        Reserva nueva = reservaEjemplo(null, "Aniversario");
        when(repo.save(nueva)).thenReturn(reservaEjemplo(5L, "Aniversario"));

        Reserva resultado = service.crear(nueva);

        assertEquals(5L, resultado.getId());
        verify(repo).save(nueva);
    }
}
