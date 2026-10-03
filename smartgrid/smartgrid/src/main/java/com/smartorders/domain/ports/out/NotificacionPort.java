package com.smartorders.domain.ports.out;

import com.smartorders.domain.model.Pedido;

public interface NotificacionPort {
    void notificarCreacionPedido(Pedido pedido);
    void notificarCambioEstado(Pedido pedido, String estadoAnterior);
}
