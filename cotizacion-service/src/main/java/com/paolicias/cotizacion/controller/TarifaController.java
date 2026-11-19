package com.paolicias.cotizacion.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paolicias.cotizacion.entities.Tarifa;
import com.paolicias.cotizacion.services.CotizacionService;

@RestController
@RequestMapping("/api/tarifas")
public class TarifaController {

    private final CotizacionService cotizacionService;

    public TarifaController(CotizacionService cotizacionService) {
        this.cotizacionService = cotizacionService;
    }

    // GET /api/tarifas  ->  tramos de tarifa ordenados por invitados
    @GetMapping
    public List<Tarifa> listarTarifas() {
        return cotizacionService.tarifas();
    }
}
