package com.paolicias.catalogo.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.paolicias.catalogo.entities.Producto;
import com.paolicias.catalogo.repository.ProductoRepository;

// Pruebas unitarias del servicio: el repositorio se simula con Mockito (no usa la base de datos).
class ProductoServiceImplTest {

    private ProductoRepository repo;
    private ProductoServiceImpl service;

    @BeforeEach
    void setUp() {
        repo = mock(ProductoRepository.class);
        service = new ProductoServiceImpl(repo);
    }

    private Producto productoEjemplo(Long id, String nombre) {
        return new Producto(id, nombre, "Descripcion de prueba", "Tortas", 32000, 12, "tortatresleches");
    }

    @Test
    void obtenerTodos_devuelveLaListaDelRepositorio() {
        List<Producto> productos = List.of(
                productoEjemplo(1L, "Torta Tres Leches"),
                productoEjemplo(2L, "Torta de Chocolate"));
        when(repo.findAll()).thenReturn(productos);

        List<Producto> resultado = service.obtenerTodos();

        assertEquals(2, resultado.size());
        assertEquals("Torta Tres Leches", resultado.get(0).getNombre());
    }

    @Test
    void obtenerPorId_conIdExistente_devuelveElProducto() {
        when(repo.findById(1L)).thenReturn(Optional.of(productoEjemplo(1L, "Torta Tres Leches")));

        Optional<Producto> resultado = service.obtenerPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals("Torta Tres Leches", resultado.get().getNombre());
    }

    @Test
    void obtenerPorId_conIdInexistente_devuelveVacio() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        Optional<Producto> resultado = service.obtenerPorId(99L);

        assertFalse(resultado.isPresent());
    }

    @Test
    void crear_guardaElProductoYLoDevuelve() {
        Producto nuevo = productoEjemplo(null, "Kuchen de Frambuesa");
        Producto guardado = productoEjemplo(5L, "Kuchen de Frambuesa");
        when(repo.save(nuevo)).thenReturn(guardado);

        Producto resultado = service.crear(nuevo);

        assertEquals(5L, resultado.getId());
        verify(repo).save(nuevo);
    }
}
