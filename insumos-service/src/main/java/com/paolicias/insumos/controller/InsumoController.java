package com.paolicias.insumos.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paolicias.insumos.dto.CalculoInsumos;
import com.paolicias.insumos.dto.CalculoRequest;
import com.paolicias.insumos.dto.ConsumoRequest;
import com.paolicias.insumos.dto.StockRequest;
import com.paolicias.insumos.entities.Insumo;
import com.paolicias.insumos.services.InsumoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/insumos")
public class InsumoController {

    private final InsumoService insumoService;

    public InsumoController(InsumoService insumoService) {
        this.insumoService = insumoService;
    }

    // GET /api/insumos  ->  inventario ordenado por nombre (pantalla Insumos de la admin)
    @GetMapping
    public List<Insumo> listarInsumos() {
        return insumoService.listar();
    }

    // PATCH /api/insumos/{id}/stock  ->  la admin ajusta el stock (por ejemplo, después de comprar)
    @PatchMapping("/{id}/stock")
    public Insumo actualizarStock(@PathVariable Long id, @Valid @RequestBody StockRequest request) {
        return insumoService.actualizarStock(id, request.stock());
    }

    // POST /api/insumos/calculo  ->  cuánto se necesita de cada insumo y si alcanza (no descuenta)
    @PostMapping("/calculo")
    public CalculoInsumos calcular(@Valid @RequestBody CalculoRequest request) {
        return insumoService.calcular(request.lineas());
    }

    // POST /api/insumos/consumos  ->  descuenta los insumos de una reserva (409 si falta algo)
    @PostMapping("/consumos")
    public CalculoInsumos consumir(@Valid @RequestBody ConsumoRequest request) {
        return insumoService.consumir(request.reservaId(), request.lineas());
    }
}
