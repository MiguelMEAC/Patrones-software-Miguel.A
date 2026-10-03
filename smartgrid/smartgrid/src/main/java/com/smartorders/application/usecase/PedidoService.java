package com.smartorders.application.usecase;

import com.smartorders.application.facade.PedidoProcesamientoFacade;
import com.smartorders.domain.model.Pedido;
import com.smartorders.domain.ports.in.ConsultarPedidoUseCase;
import com.smartorders.domain.ports.in.CrearPedidoUseCase;
import com.smartorders.domain.ports.out.PedidoRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PedidoService implements CrearPedidoUseCase, ConsultarPedidoUseCase {

    private final PedidoProcesamientoFacade facade;
    private final PedidoRepositoryPort pedidoRepository;

    public PedidoService(PedidoProcesamientoFacade facade, PedidoRepositoryPort pedidoRepository) {
        this.facade = facade;
        this.pedidoRepository = pedidoRepository;
    }

    @Override
    public Pedido crearPedido(String clienteId, List<ItemEntrada> items, String observaciones) {
        return facade.procesarCreacionPedido(clienteId, items, observaciones);
    }

    @Override
    public Optional<Pedido> obtenerPorId(String id) {
        return pedidoRepository.buscarPorId(id);
    }

    @Override
    public List<Pedido> listarTodos() {
        return pedidoRepository.listarTodos();
    }

    @Override
    public List<Pedido> listarPorCliente(String clienteId) {
        return pedidoRepository.listarPorClienteId(clienteId);
    }
}
