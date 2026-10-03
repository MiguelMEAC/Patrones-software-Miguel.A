package com.smartorders.domain.ports.in;

import com.smartorders.domain.model.Pedido;
import java.util.List;

public interface CrearPedidoUseCase {
    Pedido crearPedido(String clienteId, List<ItemEntrada> items, String observaciones);

    record ItemEntrada(String productoId, String nombreProducto, int cantidad, double precioUnitario) {}
}
