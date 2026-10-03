package com.smartorders.domain.ports.in;

public interface FacturarPedidoUseCase {
    ResultadoFactura generarFactura(String pedidoId, String tipoFactura);

    record ResultadoFactura(String numeroFactura, String tipo, String detalle, double total, String codigoFiscal) {}
}
