package com.smartorders.infrastructure.observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Observador Concreto: Registra trazas de telemetría y métricas para Prometheus y Grafana.
 */
@Component
public class AuditoriaObserver implements PedidoObserver {

    private static final Logger log = LoggerFactory.getLogger(AuditoriaObserver.class);

    @Override
    public String getNombreObserver() {
        return "AUDITORIA_TELEMETRIA_OBSERVER";
    }

    @Override
    public void onPedidoEvent(PedidoEvent event) {
        log.info("[METRICA_PROMETHEUS] metric=pedidos_eventos_total event_type={} pedido_id={} total={} timestamp={}",
                event.tipoEvento(), event.pedido().getId(), event.pedido().getMontoTotal(), event.timestamp());
    }
}
