package com.paolicias.insumos.seed;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.paolicias.insumos.entities.Insumo;
import com.paolicias.insumos.entities.Receta;
import com.paolicias.insumos.repository.InsumoRepository;
import com.paolicias.insumos.repository.RecetaRepository;

// Carga el inventario y las recetas de ejemplo la primera vez (tablas vacías).
// Los productoId son los del catálogo inicial (ProductoSeeder de catalogo-service, en el mismo orden).
// Cantidades y stock son de ejemplo.
@Component
public class InsumoSeeder implements CommandLineRunner {

    private final InsumoRepository insumoRepo;
    private final RecetaRepository recetaRepo;

    public InsumoSeeder(InsumoRepository insumoRepository, RecetaRepository recetaRepository) {
        this.insumoRepo = insumoRepository;
        this.recetaRepo = recetaRepository;
    }

    @Override
    public void run(String... args) {
        if (insumoRepo.count() > 0) {
            return;
        }

        Map<String, Insumo> i = new LinkedHashMap<>();
        for (Insumo insumo : List.of(
                new Insumo("Harina", "kg", 20.0),
                new Insumo("Azúcar", "kg", 15.0),
                new Insumo("Huevos", "unidades", 120.0),
                new Insumo("Mantequilla", "kg", 6.0),
                new Insumo("Leche", "L", 15.0),
                new Insumo("Crema", "L", 10.0),
                new Insumo("Frutos rojos", "kg", 3.0),
                new Insumo("Frambuesas", "kg", 2.0),
                new Insumo("Arándanos", "kg", 1.5),
                new Insumo("Chocolate", "kg", 4.0),
                new Insumo("Queso crema", "kg", 4.0),
                new Insumo("Limones", "unidades", 30.0),
                new Insumo("Mascarpone", "kg", 2.0),
                new Insumo("Café", "kg", 0.5),
                new Insumo("Pulpa de maracuyá", "kg", 2.0),
                new Insumo("Carne molida", "kg", 8.0),
                new Insumo("Pasta para lasaña", "kg", 3.0),
                new Insumo("Queso rallado", "kg", 3.0),
                new Insumo("Pollo", "kg", 8.0),
                new Insumo("Champiñones", "kg", 2.0),
                new Insumo("Choclo", "kg", 10.0))) {
            i.put(insumo.getNombre(), insumoRepo.save(insumo));
        }

        recetaRepo.saveAll(List.of(
                // 1 Torta Frutos Rojos (rinde 12)
                new Receta(1L, i.get("Harina"), 0.5), new Receta(1L, i.get("Azúcar"), 0.4),
                new Receta(1L, i.get("Huevos"), 6.0), new Receta(1L, i.get("Crema"), 0.5),
                new Receta(1L, i.get("Frutos rojos"), 0.4), new Receta(1L, i.get("Mantequilla"), 0.2),
                // 2 Torta Tres Leches (rinde 12)
                new Receta(2L, i.get("Harina"), 0.4), new Receta(2L, i.get("Azúcar"), 0.5),
                new Receta(2L, i.get("Huevos"), 8.0), new Receta(2L, i.get("Leche"), 1.2),
                new Receta(2L, i.get("Crema"), 0.3),
                // 3 Torta de Chocolate (rinde 12)
                new Receta(3L, i.get("Harina"), 0.4), new Receta(3L, i.get("Azúcar"), 0.4),
                new Receta(3L, i.get("Huevos"), 6.0), new Receta(3L, i.get("Chocolate"), 0.4),
                new Receta(3L, i.get("Mantequilla"), 0.25), new Receta(3L, i.get("Crema"), 0.3),
                // 4 Kuchen de Frambuesa (rinde 8)
                new Receta(4L, i.get("Harina"), 0.35), new Receta(4L, i.get("Azúcar"), 0.2),
                new Receta(4L, i.get("Huevos"), 3.0), new Receta(4L, i.get("Mantequilla"), 0.2),
                new Receta(4L, i.get("Leche"), 0.5), new Receta(4L, i.get("Frambuesas"), 0.3),
                // 5 Pie de Limón (rinde 8)
                new Receta(5L, i.get("Harina"), 0.25), new Receta(5L, i.get("Azúcar"), 0.3),
                new Receta(5L, i.get("Huevos"), 4.0), new Receta(5L, i.get("Mantequilla"), 0.15),
                new Receta(5L, i.get("Limones"), 6.0),
                // 6 Cheesecake Arándanos (rinde 10)
                new Receta(6L, i.get("Queso crema"), 0.6), new Receta(6L, i.get("Azúcar"), 0.25),
                new Receta(6L, i.get("Huevos"), 3.0), new Receta(6L, i.get("Mantequilla"), 0.12),
                new Receta(6L, i.get("Arándanos"), 0.25),
                // 7 Tiramisú en Vaso (individual)
                new Receta(7L, i.get("Mascarpone"), 0.06), new Receta(7L, i.get("Café"), 0.01),
                new Receta(7L, i.get("Azúcar"), 0.02), new Receta(7L, i.get("Huevos"), 0.5),
                // 8 Mousse de Maracuyá (individual)
                new Receta(8L, i.get("Pulpa de maracuyá"), 0.05), new Receta(8L, i.get("Crema"), 0.06),
                new Receta(8L, i.get("Azúcar"), 0.02),
                // 9 Lasaña de Carne (individual)
                new Receta(9L, i.get("Carne molida"), 0.15), new Receta(9L, i.get("Pasta para lasaña"), 0.08),
                new Receta(9L, i.get("Queso rallado"), 0.05), new Receta(9L, i.get("Leche"), 0.1),
                // 10 Pollo al Champiñón (individual)
                new Receta(10L, i.get("Pollo"), 0.2), new Receta(10L, i.get("Champiñones"), 0.08),
                new Receta(10L, i.get("Crema"), 0.05),
                // 11 Pastel de Choclo (individual)
                new Receta(11L, i.get("Choclo"), 0.25), new Receta(11L, i.get("Carne molida"), 0.1),
                new Receta(11L, i.get("Pollo"), 0.05), new Receta(11L, i.get("Huevos"), 0.5)));
    }
}
