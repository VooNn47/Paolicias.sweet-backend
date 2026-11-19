package com.paolicias.insumos.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

// Tabla "consumos": registra que los insumos de una reserva ya se descontaron del stock.
// La clave es el id de la reserva, así una misma reserva nunca descuenta dos veces.
@Data
@NoArgsConstructor
@Entity
@Table(name = "consumos")
public class Consumo {

    @Id
    private Long reservaId;

    @Column(nullable = false)
    private LocalDateTime fecha;

    // Resumen de lo descontado, ej: "Harina 1.5 kg; Huevos 18 unidades"
    @Column(length = 2000)
    private String detalle;

    public Consumo(Long reservaId, LocalDateTime fecha, String detalle) {
        this.reservaId = reservaId;
        this.fecha = fecha;
        this.detalle = detalle;
    }
}
