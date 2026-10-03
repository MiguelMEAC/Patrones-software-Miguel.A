package com.smartorders.infrastructure.factory;

import com.smartorders.domain.model.Pedido;

/**
 * Creador Abstracto (Factory Method):
 * Declara el método fábrica abstracto crearFactura() y provee lógica de orquestación de negocio.
 */
public abstract class FacturaFactory {

    // ===== FACTORY METHOD =====
    public abstract Factura crearFactura();

    public abstract String getTipoFactory();

    /**
     * Operación que utiliza el producto abstracto generado por el factory method.
     */
    public String procesarYEmitir(Pedido pedido) {
        Factura factura = crearFactura();
        return factura.generarDetalleFactura(pedido);
    }
}
