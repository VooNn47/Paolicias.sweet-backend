package com.paolicias.catalogo.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

// Tabla "productos". Los campos son los mismos que ProductoEntity en Room (Android).
// "porciones" = a cuántas personas rinde UNA unidad. "imagen" = nombre del drawable en la app.
@Data
@NoArgsConstructor
@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Ingresa el nombre")
    private String nombre;

    @Size(max = 500)
    @Column(length = 500)
    private String descripcion;

    @NotBlank(message = "Ingresa la categoría")
    private String categoria;

    @NotNull(message = "Ingresa el precio")
    @Min(value = 1, message = "El precio debe ser mayor que 0")
    private Integer precio;

    @NotNull(message = "Ingresa las porciones")
    @Min(value = 1, message = "Las porciones deben ser al menos 1")
    private Integer porciones;

    private String imagen;

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
