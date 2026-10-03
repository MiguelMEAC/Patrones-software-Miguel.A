package com.smartorders.domain.state;

import com.smartorders.domain.model.Pedido;

/**
 * Estado Aprobado: El pedido paso la validacion de credito y politicas comerciales.
 */
public class EstadoAprobado implements EstadoPedido {

    @Override
    public String getNombre() {
        return "APROBADO";
    }

    @Override
    public void procesar(Pedido pedido) {
        // Ya esta aprobado
    }

    @Override
    public void aprobar(Pedido pedido) {
        // Idempotente
    }

    @Override
    public void rechazar(Pedido pedido, String motivo) {
        throw new IllegalStateException("No se puede rechazar un pedido que ya fue aprobado previamente");
    }

    @Override
    public void completar(Pedido pedido) {
        pedido.cambiarEstado(new EstadoCompletado());
    }

    @Override
    public void cancelar(Pedido pedido) {
        pedido.cambiarEstado(new EstadoCancelado());
    }
}
