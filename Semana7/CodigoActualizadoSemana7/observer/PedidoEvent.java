package com.smartorders.infrastructure.observer;

import com.smartorders.domain.model.Pedido;
import java.time.LocalDateTime;

/**
 * Objeto de Evento que encapsula los datos transmitidos en el patron Observer.
 */
public record PedidoEvent(Pedido pedido, TipoEvento tipoEvento, String mensaje, LocalDateTime timestamp) {

    public enum TipoEvento {
        PEDIDO_CREADO,
        PEDIDO_APROBADO,
        PEDIDO_RECHAZADO,
        PEDIDO_COMPLETADO,
        PEDIDO_CANCELADO
    }

    public static PedidoEvent of(Pedido pedido, TipoEvento tipo, String mensaje) {
        return new PedidoEvent(pedido, tipo, mensaje, LocalDateTime.now());
    }
}
