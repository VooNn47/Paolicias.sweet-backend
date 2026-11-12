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
        return productoRepo.findAll();
    }

    @Override
    public Optional<Producto> obtenerPorId(Long id) {
        return productoRepo.findById(id);
    }

    @Override
    public Producto crear(Producto producto) {
        return productoRepo.save(producto);
    }

}
