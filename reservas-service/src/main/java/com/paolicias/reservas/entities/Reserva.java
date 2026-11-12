package com.paolicias.reservas.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

// Tabla "reservas". Ajustar los campos a los de ReservaEntity de la app (Room).
// "estadoAbono" lo cambia el administrador desde el panel de admin.
@Data
@Entity
@Table(name = "reservas")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombreCliente;
    private String correoCliente;
    private String fechaEvento;
    private Integer cantidadPersonas;
    @Column(length = 500)
    private String detalle;
    private Integer total;
    private String estadoAbono;

    public Reserva() {

    }

    public Reserva(Long id, String nombreCliente, String correoCliente, String fechaEvento,
                   Integer cantidadPersonas, String detalle, Integer total, String estadoAbono) {
        this.id = id;
        this.nombreCliente = nombreCliente;
        this.correoCliente = correoCliente;
        this.fechaEvento = fechaEvento;
        this.cantidadPersonas = cantidadPersonas;
        this.detalle = detalle;
        this.total = total;
        this.estadoAbono = estadoAbono;
    }
}
