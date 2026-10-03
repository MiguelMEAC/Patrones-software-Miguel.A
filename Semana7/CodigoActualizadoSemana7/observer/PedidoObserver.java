package com.smartorders.infrastructure.observer;

/**
 * Patrón de Comportamiento: Observer (GoF)
 * Interfaz Observador: Define el método de actualización invocado cuando cambia el estado de un pedido.
 */
public interface PedidoObserver {

    String getNombreObserver();

    void onPedidoEvent(PedidoEvent event);
}
