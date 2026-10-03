package com.smartorders.infrastructure.factory;

import com.smartorders.domain.model.Pedido;
import java.util.UUID;

/**
 * Producto Concreto: Factura electrónica obligatoria con firma fiscal, CUFE y discriminación tributaria.
 */
public class FacturaElectronicaFiscal implements Factura {

    private final String cufe;

    public FacturaElectronicaFiscal() {
        this.cufe = "CUFE-" + UUID.randomUUID().toString().toUpperCase();
    }

    @Override
    public String getTipo() {
        return "ELECTRONICA_FISCAL";
    }

    @Override
    public String generarDetalleFactura(Pedido pedido) {
        return String.format("FACTURA ELECTRONICA DIAN #FE-%s | NIT/CC: %s | Cliente: %s | Subtotal: $%,.2f | Descuento: $%,.2f | IVA (19%%): $%,.2f | Total: $%,.2f | CUFE: %s",
                pedido.getCodigo(),
                pedido.getCliente().getDatosFiscales().getNitORut(),
                pedido.getCliente().getNombre(),
                pedido.getMontoBruto(),
                pedido.getMontoDescuento(),
                calcularImpuestos(pedido),
                pedido.getMontoTotal(),
                this.cufe);
    }

    @Override
    public double calcularImpuestos(Pedido pedido) {
        return Math.round(pedido.getMontoTotal() * 0.19 * 100.0) / 100.0;
    }

    @Override
    public String getCodigoFiscal() {
        return this.cufe;
    }
}
