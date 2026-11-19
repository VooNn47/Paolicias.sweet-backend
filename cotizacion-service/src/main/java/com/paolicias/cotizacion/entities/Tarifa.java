package com.paolicias.cotizacion.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

// Tabla "tarifas": cuánto cobra el servicio de banquetería según la cantidad de invitados.
// Ej: de 21 a 30 invitados -> $80.000
@Data
@NoArgsConstructor
@Entity
@Table(name = "tarifas")
public class Tarifa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer minInvitados;

    @Column(nullable = false)
    private Integer maxInvitados;

    @Column(nullable = false)
    private Integer monto;

    public Tarifa(Long id, Integer minInvitados, Integer maxInvitados, Integer monto) {
        this.id = id;
        this.minInvitados = minInvitados;
        this.maxInvitados = maxInvitados;
        this.monto = monto;
    }
}
