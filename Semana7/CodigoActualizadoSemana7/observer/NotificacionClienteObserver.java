package com.smartorders.infrastructure.observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Observador Concreto: Envia alertas por correo electrónico o SMS al cliente ante cambios de estado.
 */
@Component
public class NotificacionClienteObserver implements PedidoObserver {

    private static final Logger log = LoggerFactory.getLogger(NotificacionClienteObserver.class);

    @Override
    public String getNombreObserver() {
        return "NOTIFICACION_CLIENTE_OBSERVER";
    }

    @Override
    public void onPedidoEvent(PedidoEvent event) {
        String email = event.pedido().getCliente().getEmail();
        String cliente = event.pedido().getCliente().getNombre();
        String codigo = event.pedido().getCodigo();

        log.info("[NOTIFICACION_EMAIL] Destinatario: <{}> ({}) | Asunto: Actualizacion Pedido {} | Estado: {} | Mensaje: {}",
                email, cliente, codigo, event.tipoEvento(), event.mensaje());
    }
}
