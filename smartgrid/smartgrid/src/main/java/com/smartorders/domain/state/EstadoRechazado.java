package com.smartorders.domain.state;

import com.smartorders.domain.model.Pedido;

/**
 * Estado Rechazado: Pedido no admitido por insolvencia, mora o limite de credito excedido.
 */
public class EstadoRechazado implements EstadoPedido {

    @Override
    public String getNombre() {
        return "RECHAZADO";
    }

    @Override
    public void procesar(Pedido pedido) {
        throw new IllegalStateException("No se puede procesar un pedido rechazado");
    }

    @Override
    public void aprobar(Pedido pedido) {
        throw new IllegalStateException("No se puede aprobar un pedido en estado rechazado");
    }

    @Override
    public void rechazar(Pedido pedido, String motivo) {
        // Idempotente
    }

    @Override
    public void completar(Pedido pedido) {
        throw new IllegalStateException("Un pedido rechazado no puede ser completado");
    }

    @Override
    public void cancelar(Pedido pedido) {
        // Estado final
    }
}
