package com.smartorders.infrastructure.factory;

import org.springframework.stereotype.Component;

/**
 * Creador Concreto para Facturas Electronicas Fiscales.
 */
@Component("facturaElectronicaFiscalFactory")
public class FacturaElectronicaFiscalFactory extends FacturaFactory {

    @Override
    public Factura crearFactura() {
        return new FacturaElectronicaFiscal();
    }

    @Override
    public String getTipoFactory() {
        return "ELECTRONICA_FISCAL";
    }
}
