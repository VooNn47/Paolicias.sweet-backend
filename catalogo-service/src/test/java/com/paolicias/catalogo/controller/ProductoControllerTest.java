package com.paolicias.catalogo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import com.paolicias.catalogo.entities.Producto;
import com.paolicias.catalogo.services.ProductoService;

// Pruebas unitarias del controller: el servicio se simula con Mockito.
class ProductoControllerTest {

    private ProductoService service;
    private ProductoController controller;

    @BeforeEach
    void setUp() {
        service = mock(ProductoService.class);
        controller = new ProductoController(service);
    }

    private Producto productoEjemplo(Long id) {
        return new Producto(id, "Pie de Limon", "Base de galleta y merengue", "Pasteles", 20000, 8, "pielimon");
    }

    @Test
    void listarProductos_devuelveElCatalogo() {
        when(service.obtenerTodos()).thenReturn(List.of(productoEjemplo(1L), productoEjemplo(2L)));

        List<Producto> resultado = controller.listarProductos();

        assertEquals(2, resultado.size());
    }

    @Test
    void obtenerPorId_conIdExistente_responde200() {
        when(service.obtenerPorId(1L)).thenReturn(Optional.of(productoEjemplo(1L)));

        ResponseEntity<Producto> respuesta = controller.obtenerPorId(1L);

        assertEquals(200, respuesta.getStatusCode().value());
        assertEquals("Pie de Limon", respuesta.getBody().getNombre());
    }

    @Test
    void obtenerPorId_conIdInexistente_responde404() {
        when(service.obtenerPorId(99L)).thenReturn(Optional.empty());

        ResponseEntity<Producto> respuesta = controller.obtenerPorId(99L);

        assertEquals(404, respuesta.getStatusCode().value());
        assertNull(respuesta.getBody());
    }

    @Test
    void guardarProducto_devuelveElProductoCreado() {
        Producto nuevo = productoEjemplo(null);
        when(service.crear(nuevo)).thenReturn(productoEjemplo(7L));

        Producto resultado = controller.guardarProducto(nuevo);

        assertEquals(7L, resultado.getId());
    }
}
