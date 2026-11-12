package com.paolicias.catalogo.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

// Tabla "productos". Los campos son los mismos que tenía ProductoEntity en Room (Android).
// "porciones" = a cuántas personas rinde UNA unidad. "imagen" = nombre del drawable en la app.
@Data
@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    @Column(length = 500)
    private String descripcion;
    private String categoria;
    private Integer precio;
    private Integer porciones;
    private String imagen;

    public Producto() {

    }

    public Producto(Long id, String nombre, String descripcion, String categoria,
                    Integer precio, Integer porciones, String imagen) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.precio = precio;
        this.porciones = porciones;
        this.imagen = imagen;
    }
}
