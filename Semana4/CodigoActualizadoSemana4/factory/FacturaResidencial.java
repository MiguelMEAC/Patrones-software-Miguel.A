package com.smartgrid.smartgrid.factory;

public class FacturaResidencial implements Factura {

    private static final double TARIFA = 500;

    @Override
    public String generarFactura(double consumo) {

        double total = consumo * TARIFA;

        return "FACTURA RESIDENCIAL\n" +
                "Consumo: " + consumo + " kWh\n" +
                "Tarifa: $" + TARIFA + " por kWh\n" +
                "Total: $" + total;
    }
}