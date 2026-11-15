package com.paolicias.reservas.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

// Tabla "reservas". Los campos son los mismos que tiene ReservaEntity en Room (Android).
// "productos" = ids de productos separados por coma. "estadoAbono" lo cambia el admin.
@Data
@Entity
@Table(name = "reservas")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombreEvento;
    private String fecha;
    private String horario;
    private String invitados;
    @Column(length = 500)
    private String observaciones;
    private String correoCliente;
    private String estadoAbono;
    @Column(length = 500)
    private String productos;
    private Integer total;
    @Column(length = 500)
    private String comprobante;
    private String fechaPago;

    public Reserva() {

    }
}