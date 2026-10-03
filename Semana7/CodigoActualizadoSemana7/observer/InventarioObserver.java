package com.smartorders.infrastructure.observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Observador Concreto: Gestiona las reservas y liberaciones de stock físico en el almacén.
 */
@Component
public class InventarioObserver implements PedidoObserver {

    private static final Logger log = LoggerFactory.getLogger(InventarioObserver.class);

    @Override
    public String getNombreObserver() {
        return "INVENTARIO_OBSERVER";
    }

    @Override
    public void onPedidoEvent(PedidoEvent event) {
        switch (event.tipoEvento()) {
            case PEDIDO_CREADO -> log.info("[INVENTARIO] Stock reservado temporalmente para {} items del pedido {}",
                    event.pedido().getItems().size(), event.pedido().getCodigo());
            case PEDIDO_APROBADO -> log.info("[INVENTARIO] Stock confirmado para despacho del pedido {}",
                    event.pedido().getCodigo());
            case PEDIDO_RECHAZADO, PEDIDO_CANCELADO -> log.warn("[INVENTARIO] Stock liberado de vuelta a bodega para pedido {}",
                    event.pedido().getCodigo());
            case PEDIDO_COMPLETADO -> log.info("[INVENTARIO] Descuento permanente de inventario completado para {}",
                    event.pedido().getCodigo());
        }
    }
}
