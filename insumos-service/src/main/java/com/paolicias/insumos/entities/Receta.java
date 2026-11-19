package com.paolicias.insumos.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.NoArgsConstructor;

// Tabla "recetas": cuánto de cada insumo lleva UNA unidad de un producto del catálogo.
// Ej: 1 Torta Frutos Rojos (productoId 1) lleva 0,4 kg de Frutos rojos.
@Data
@NoArgsConstructor
@Entity
@Table(name = "recetas", uniqueConstraints = @UniqueConstraint(columnNames = {"productoId", "insumo_id"}))
public class Receta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Id del producto en catalogo-service
    @Column(nullable = false)
    private Long productoId;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "insumo_id")
    private Insumo insumo;

    @Column(nullable = false)
    private Double cantidadPorUnidad;

    public Receta(Long productoId, Insumo insumo, Double cantidadPorUnidad) {
        this.productoId = productoId;
        this.insumo = insumo;
        this.cantidadPorUnidad = cantidadPorUnidad;
    }
}
