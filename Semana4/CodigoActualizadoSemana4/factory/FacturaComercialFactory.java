package com.smartgrid.smartgrid.factory;

import org.springframework.stereotype.Component;

@Component
public class FacturaComercialFactory extends FacturaFactory {

    @Override
    public Factura crearFactura() {

        return new FacturaComercial();
    }

    @Override
    public String getTipo() {

        return "COMERCIAL";
    }
}