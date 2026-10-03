package com.smartorders.infrastructure.factory;

import org.springframework.stereotype.Component;

/**
 * Creador Concreto para Facturas Estandar.
 */
@Component("facturaEstandarFactory")
public class FacturaEstandarFactory extends FacturaFactory {

    @Override
    public Factura crearFactura() {
        return new FacturaEstandar();
    }

    @Override
    public String getTipoFactory() {
        return "ESTANDAR";
    }
}
