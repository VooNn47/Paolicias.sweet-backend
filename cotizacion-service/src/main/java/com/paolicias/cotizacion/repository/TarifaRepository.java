package com.paolicias.cotizacion.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.paolicias.cotizacion.entities.Tarifa;

public interface TarifaRepository extends JpaRepository<Tarifa, Long> {

    // La tarifa cuyo tramo contiene la cantidad de invitados (min <= invitados <= max)
    Optional<Tarifa> findFirstByMinInvitadosLessThanEqualAndMaxInvitadosGreaterThanEqual(
            Integer invitados, Integer invitadosOtraVez);

    List<Tarifa> findAllByOrderByMinInvitadosAsc();
}
