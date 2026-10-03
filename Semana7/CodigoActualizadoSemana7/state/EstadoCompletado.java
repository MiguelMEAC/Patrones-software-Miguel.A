package com.smartorders.domain.state;

import com.smartorders.domain.model.Pedido;

/**
 * Estado Completado: Pedido despachado, facturado y cerrado con exito.
 */
public class EstadoCompletado implements EstadoPedido {

    @Override
    public String getNombre() {
        return "COMPLETADO";
    }

    @Override
    public void procesar(Pedido pedido) {
        throw new IllegalStateException("El pedido ya se encuentra completado");
    }

    @Override
    public void aprobar(Pedido pedido) {
        // Ya completado
    }

    @Override
    public void rechazar(Pedido pedido, String motivo) {
        throw new IllegalStateException("Un pedido completado no puede ser rechazado");
    }

    @Override
    public void completar(Pedido pedido) {
        // Idempotente
    }

    @Override
    public void cancelar(Pedido pedido) {
        throw new IllegalStateException("Un pedido completado no puede cancelarse directamente");
    }
}
