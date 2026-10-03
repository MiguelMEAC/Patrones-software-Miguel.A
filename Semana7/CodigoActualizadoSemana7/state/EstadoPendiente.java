package com.smartorders.domain.state;

import com.smartorders.domain.model.Pedido;

/**
 * Estado Inicial: El pedido ha sido registrado pero aun no evaluado ni aprobado.
 */
public class EstadoPendiente implements EstadoPedido {

    @Override
    public String getNombre() {
        return "PENDIENTE";
    }

    @Override
    public void procesar(Pedido pedido) {
        // Permanece en proceso de validacion
    }

    @Override
    public void aprobar(Pedido pedido) {
        pedido.cambiarEstado(new EstadoAprobado());
    }

    @Override
    public void rechazar(Pedido pedido, String motivo) {
        pedido.setMotivoRechazo(motivo);
        pedido.cambiarEstado(new EstadoRechazado());
    }

    @Override
    public void completar(Pedido pedido) {
        throw new IllegalStateException("Un pedido pendiente no puede completarse directamente sin aprobacion previa");
    }

    @Override
    public void cancelar(Pedido pedido) {
        pedido.cambiarEstado(new EstadoCancelado());
    }
}
