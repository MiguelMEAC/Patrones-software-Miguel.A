package com.smartorders.infrastructure.factory;

import com.smartorders.domain.model.Pedido;

/**
 * Patrón Factory Method (GoF - Creacional)
 * Producto Abstracto: Define el contrato común para la emisión de facturas del sistema.
 */
public interface Factura {

    String getTipo();

    String generarDetalleFactura(Pedido pedido);

    double calcularImpuestos(Pedido pedido);

    String getCodigoFiscal();
}
