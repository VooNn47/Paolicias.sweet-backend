package com.paolicias.catalogo.seed;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.paolicias.catalogo.entities.Producto;
import com.paolicias.catalogo.repository.ProductoRepository;

// Carga los 11 productos de ejemplo la primera vez (tabla vacía).
// Es el mismo catálogo semilla que tenía la app en Room. Los precios son de ejemplo.
@Component
public class ProductoSeeder implements CommandLineRunner {

    private final ProductoRepository productoRepo;

    public ProductoSeeder(ProductoRepository productoRepository) {
        this.productoRepo = productoRepository;
    }

    @Override
    public void run(String... args) {
        if (productoRepo.count() == 0) {
            productoRepo.saveAll(List.of(
                new Producto(null, "Torta Frutos Rojos",
                        "Torta de bizcocho de vainilla con crema y frutos rojos.",
                        "Tortas", 45000, 12, "tortafrutosrojos"),
                new Producto(null, "Torta Tres Leches",
                        "Bizcocho esponjoso bañado en tres leches con merengue.",
                        "Tortas", 32000, 12, "tortatresleches"),
                new Producto(null, "Torta de Chocolate",
                        "Bizcocho de chocolate húmedo con ganache semi amargo.",
                        "Tortas", 40000, 12, "tortachocolate"),
                new Producto(null, "Kuchen de Frambuesa",
                        "Masa quebrada con crema pastelera y frambuesas.",
                        "Pasteles", 22000, 8, "kuchenframbuesa"),
                new Producto(null, "Pie de Limón",
                        "Base de galleta, crema de limón y merengue tostado.",
                        "Pasteles", 20000, 8, "pielimon"),
                new Producto(null, "Cheesecake Arándanos",
                        "Cheesecake cremoso con coulis de arándanos.",
                        "Postres", 38000, 10, "cheskake"),
                new Producto(null, "Tiramisú en Vaso",
                        "Postre individual de café, mascarpone y cacao.",
                        "Postres", 2800, 1, "tiramisu"),
                new Producto(null, "Mousse de Maracuyá",
                        "Postre individual suave y ácido de maracuyá.",
                        "Postres", 2500, 1, "moussemaracuya"),
                new Producto(null, "Lasaña de Carne",
                        "Lasaña con salsa boloñesa, bechamel y queso gratinado.",
                        "Platos de fondo", 6500, 1, "lasana"),
                new Producto(null, "Pollo al Champiñón",
                        "Pechuga de pollo en salsa cremosa de champiñones.",
                        "Platos de fondo", 6900, 1, "pollochampinon"),
                new Producto(null, "Pastel de Choclo",
                        "Pino de carne, pollo y huevo bajo una capa de choclo.",
                        "Platos de fondo", 5900, 1, "pastelchoclo")
            ));
        }
    }
}
