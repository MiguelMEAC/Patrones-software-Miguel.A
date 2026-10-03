package com.smartgrid.smartgrid.controller;

import com.smartgrid.smartgrid.service.FacturacionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/facturacion")
public class FacturacionController {

    private final FacturacionService facturacionService;

    public FacturacionController(FacturacionService facturacionService) {
        this.facturacionService = facturacionService;
    }

    @GetMapping("/generar")
    public String generarFactura(
            @RequestParam String tipo,
            @RequestParam double consumo) {

        return facturacionService.generarFactura(tipo, consumo);
    }
}