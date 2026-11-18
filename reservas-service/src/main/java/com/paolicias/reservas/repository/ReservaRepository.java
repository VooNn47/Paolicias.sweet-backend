package com.paolicias.reservas.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.paolicias.reservas.entities.Reserva;

// Todas las listas salen ordenadas por fecha y horario (orden cronológico real)
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByCorreoClienteOrderByFechaAscHorarioAsc(String correoCliente);

    List<Reserva> findAllByOrderByFechaAscHorarioAsc();

    List<Reserva> findByEstadoAbonoOrderByFechaAscHorarioAsc(String estadoAbono);

    // Para evitar el doble envío: misma reserva del mismo cliente
    boolean existsByCorreoClienteAndNombreEventoAndFechaAndHorario(
            String correoCliente, String nombreEvento, LocalDate fecha, LocalTime horario);
}
