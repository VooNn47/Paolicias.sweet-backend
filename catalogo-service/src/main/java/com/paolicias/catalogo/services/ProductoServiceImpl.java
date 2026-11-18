package com.paolicias.catalogo.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.paolicias.catalogo.entities.Producto;
import com.paolicias.catalogo.repository.ProductoRepository;

@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepo;

    public ProductoServiceImpl(ProductoRepository productoRepository) {
        this.productoRepo = productoRepository;
    }

    @Override
    public List<Producto> obtenerTodos() {
        return productoRepo.findAllByOrderByCategoriaAscNombreAsc();
    }

    @Override
    public Optional<Producto> obtenerPorId(Long id) {
        return productoRepo.findById(id);
    }

    @Override
    public Producto crear(Producto producto) {
        // Se ignora el id que venga en el JSON: así un POST nunca sobrescribe un producto existente
        producto.setId(null);
        return productoRepo.save(producto);
    }

}
