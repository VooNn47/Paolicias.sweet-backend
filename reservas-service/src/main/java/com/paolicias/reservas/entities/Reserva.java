package com.paolicias.reservas.entities;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

// Tabla "reservas". Mismos datos que ReservaEntity en Room, pero:
// - fecha y horario son tipos de fecha reales (se ordenan bien), y en JSON siguen
//   viajando como "dd/MM/yyyy" y "HH:mm", igual que en la app.
// - el total lo calcula el servidor, no la app.
// - la imagen del comprobante se guarda en el servidor; el JSON solo dice si existe.
@Data
@NoArgsConstructor
@Entity
@Table(name = "reservas")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombreEvento;

    @JsonFormat(pattern = "dd/MM/yyyy")
    @Column(nullable = false)
    private LocalDate fecha;

    @JsonFormat(pattern = "HH:mm")
    @Column(nullable = false)
    private LocalTime horario;

    @Column(nullable = false)
    private Integer invitados;

    @Column(length = 500)
    private String observaciones;

    @Column(nullable = false)
    private String correoCliente;

    @Column(nullable = false)
    private String estadoAbono = EstadoAbono.PENDIENTE;

    // Ids de los productos elegidos separados por coma (igual que en Room), ej: "1,4,7"
    @Column(length = 500)
    private String productos = "";

    // Tarifa del servicio según invitados (parte del total)
    private Integer tarifaServicio;

    @Column(nullable = false)
    private Integer total;

    // Nombre del archivo dentro de la carpeta de comprobantes (no se expone en el JSON)
    @JsonIgnore
    private String comprobante;

    @JsonIgnore
    private String comprobanteTipo;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate fechaPago;

    // ---- Campos calculados que solo aparecen en el JSON ----

    public Integer getAbono() {
        return total == null ? 0 : total / 2;
    }

    public boolean isTieneComprobante() {
        return comprobante != null && !comprobante.isBlank();
    }
}
