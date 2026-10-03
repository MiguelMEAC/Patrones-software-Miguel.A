package com.smartorders.domain.ports.out;

import com.smartorders.domain.model.Pedido;
import java.util.List;
import java.util.Optional;

public interface PedidoRepositoryPort {
    Pedido guardar(Pedido pedido);
    Optional<Pedido> buscarPorId(String id);
    Optional<Pedido> buscarPorCodigo(String codigo);
    List<Pedido> listarTodos();
    List<Pedido> listarPorClienteId(String clienteId);
}
