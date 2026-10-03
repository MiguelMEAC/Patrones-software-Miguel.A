package com.smartorders.infrastructure.observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Sujeto Concreto (Subject / Observable) del Patrón Observer:
 * Mantiene la lista de observadores suscritos y difunde los eventos de ciclo de vida del pedido.
 */
@Component
public class PedidoEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(PedidoEventPublisher.class);
    private final List<PedidoObserver> observadores = new ArrayList<>();

    public PedidoEventPublisher(List<PedidoObserver> observersInyectados) {
        if (observersInyectados != null) {
            this.observadores.addAll(observersInyectados);
        }
    }

    public synchronized void suscribir(PedidoObserver observer) {
        if (observer != null && !observadores.contains(observer)) {
            observadores.add(observer);
            log.info("[OBSERVER_REGISTRY] Observador suscrito: {}", observer.getNombreObserver());
        }
    }

    public synchronized void desuscribir(PedidoObserver observer) {
        observadores.remove(observer);
        log.info("[OBSERVER_REGISTRY] Observador desuscrito: {}", observer.getNombreObserver());
    }

    public void publicarEvento(PedidoEvent event) {
        log.info("[OBSERVER_DISPATCHER] Notificando evento [{}] para pedido {}",
                event.tipoEvento(), event.pedido().getCodigo());

        for (PedidoObserver observer : observadores) {
            try {
                observer.onPedidoEvent(event);
            } catch (Exception ex) {
                log.error("[OBSERVER_ERROR] Error en observador {}: {}",
                        observer.getNombreObserver(), ex.getMessage(), ex);
            }
        }
    }

    public List<PedidoObserver> getObservadores() {
        return List.copyOf(observadores);
    }
}
