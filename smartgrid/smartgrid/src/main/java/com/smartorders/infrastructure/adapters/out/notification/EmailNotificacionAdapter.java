package com.smartorders.infrastructure.adapters.out.notification;

import com.smartorders.domain.model.Pedido;
import com.smartorders.domain.ports.out.NotificacionPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EmailNotificacionAdapter implements NotificacionPort {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificacionAdapter.class);

    @Override
    public void notificarCreacionPedido(Pedido pedido) {
        log.info("[EMAIL_ADAPTER] Notificacion de creacion enviada a {} para pedido {}",
                pedido.getCliente().getEmail(), pedido.getCodigo());
    }

    @Override
    public void notificarCambioEstado(Pedido pedido, String estadoAnterior) {
        log.info("[EMAIL_ADAPTER] Notificacion de transicion ({} -> {}) enviada a {}",
                estadoAnterior, pedido.getEstadoNombre(), pedido.getCliente().getEmail());
    }
}
