package com.paolicias.cotizacion.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paolicias.cotizacion.dto.Cotizacion;
import com.paolicias.cotizacion.dto.CotizacionRequest;
import com.paolicias.cotizacion.services.CotizacionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cotizacion")
public class CotizacionController {

    private final CotizacionService cotizacionService;

    public CotizacionController(CotizacionService cotizacionService) {
        this.cotizacionService = cotizacionService;
    }

    // POST /api/cotizacion  ->  desglose (tarifa + productos + total + abono) antes de reservar
    @PostMapping
    public Cotizacion cotizar(@Valid @RequestBody CotizacionRequest request) {
        return cotizacionService.calcular(request.invitados(), request.productoIds());
    }
}
