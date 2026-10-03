package com.smartgrid.smartgrid.service;

import com.smartgrid.smartgrid.factory.FacturaFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FacturacionService {

    private final List<FacturaFactory> factories;

    public FacturacionService(List<FacturaFactory> factories) {
        this.factories = factories;
    }

    public String generarFactura(String tipo, double consumo) {

        return factories.stream()
                .filter(factory ->
                        factory.getTipo().equalsIgnoreCase(tipo))
                .findFirst()
                .map(factory -> factory.generar(consumo))
                .orElse("Tipo de factura no válido");
    }
}