package com.smartorders.domain.state;

import com.smartorders.domain.model.Pedido;

/**
 * Estado Cancelado: Anulacion de la transaccion.
 */
public class EstadoCancelado implements EstadoPedido {

    @Override
    public String getNombre() {
        return "CANCELADO";
    }

    @Override
    public void procesar(Pedido pedido) {
        throw new IllegalStateException("No se puede procesar un pedido cancelado");
    }

    @Override
    public void aprobar(Pedido pedido) {
        throw new IllegalStateException("No se puede aprobar un pedido cancelado");
    }

    @Override
    public void rechazar(Pedido pedido, String motivo) {
        throw new IllegalStateException("El pedido ya fue cancelado");
    }

    @Override
    public void completar(Pedido pedido) {
        throw new IllegalStateException("Un pedido cancelado no puede completarse");
    }

    @Override
    public void cancelar(Pedido pedido) {
        // Idempotente
    }
}
