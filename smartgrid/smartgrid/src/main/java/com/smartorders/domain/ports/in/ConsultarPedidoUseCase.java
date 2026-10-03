package com.smartorders.domain.ports.in;

import com.smartorders.domain.model.Pedido;
import java.util.List;
import java.util.Optional;

public interface ConsultarPedidoUseCase {
    Optional<Pedido> obtenerPorId(String id);
    List<Pedido> listarTodos();
    List<Pedido> listarPorCliente(String clienteId);
}
