package com.smartgrid.smartgrid.factory;

import org.springframework.stereotype.Component;

@Component
public class FacturaResidencialFactory extends FacturaFactory {

    @Override
    public Factura crearFactura() {

        return new FacturaResidencial();
    }

    @Override
    public String getTipo() {

        return "RESIDENCIAL";
    }
}