package com.paolicias.insumos.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

// Tabla "insumos": inventario de la administradora (ej: Harina, kg, 20.0)
@Data
@NoArgsConstructor
@Entity
@Table(name = "insumos")
public class Insumo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    // kg, L o unidades
    @Column(nullable = false)
    private String unidad;

    @Column(nullable = false)
    private Double stock;

    public Insumo(String nombre, String unidad, Double stock) {
        this.nombre = nombre;
        this.unidad = unidad;
        this.stock = stock;
    }
}
