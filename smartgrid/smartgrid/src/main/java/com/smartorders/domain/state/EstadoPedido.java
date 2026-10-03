package com.smartorders.domain.state;

import com.smartorders.domain.model.Pedido;

/**
 * Patrón State (GoF - Comportamiento):
 * Interfaz que define el comportamiento dependiente del estado del ciclo de vida del pedido.
 */
public interface EstadoPedido {

    String getNombre();

    void procesar(Pedido pedido);

    void aprobar(Pedido pedido);

    void rechazar(Pedido pedido, String motivo);

    void completar(Pedido pedido);

    void cancelar(Pedido pedido);
}
