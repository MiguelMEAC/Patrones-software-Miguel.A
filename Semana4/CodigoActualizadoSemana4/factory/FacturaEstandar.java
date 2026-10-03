package com.smartorders.infrastructure.factory;

import com.smartorders.domain.model.Pedido;

/**
 * Producto Concreto: Factura comercial simplificada para consumidor final sin requisitos tributarios complejos.
 */
public class FacturaEstandar implements Factura {

    @Override
    public String getTipo() {
        return "ESTANDAR";
    }

    @Override
    public String generarDetalleFactura(Pedido pedido) {
        return String.format("FACTURA SIMPLIFICADA #%s | Cliente: %s | Total: $%,.2f",
                pedido.getCodigo(),
                pedido.getCliente().getNombre(),
                pedido.getMontoTotal());
    }

    @Override
    public double calcularImpuestos(Pedido pedido) {
        // IVA regular 19% incluido o calculado
        return Math.round(pedido.getMontoTotal() * 0.19 * 100.0) / 100.0;
    }

    @Override
    public String getCodigoFiscal() {
        return "SIMPL-POS-" + System.currentTimeMillis();
    }
}
