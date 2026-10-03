package com.smartgrid.smartgrid.factory;

public class FacturaComercial implements Factura {

    private static final double TARIFA = 700;

    @Override
    public String generarFactura(double consumo) {

        double total = consumo * TARIFA;

        return "FACTURA COMERCIAL\n" +
                "Consumo: " + consumo + " kWh\n" +
                "Tarifa: $" + TARIFA + " por kWh\n" +
                "Total: $" + total;
    }
}