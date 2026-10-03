package com.smartorders.infrastructure.adapters.out.persistence;

import com.smartorders.domain.model.Cliente;
import com.smartorders.domain.model.ItemPedido;
import com.smartorders.domain.model.Pedido;
import com.smartorders.domain.ports.out.PedidoRepositoryPort;
import com.smartorders.domain.state.*;
import com.smartorders.infrastructure.adapters.out.persistence.entity.ItemPedidoEntity;
import com.smartorders.infrastructure.adapters.out.persistence.entity.PedidoEntity;
import com.smartorders.infrastructure.adapters.out.persistence.repository.SpringDataPedidoRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class PedidoPersistenceAdapter implements PedidoRepositoryPort {

    private final SpringDataPedidoRepository repository;
    private final ClientePersistenceAdapter clienteAdapter;

    public PedidoPersistenceAdapter(SpringDataPedidoRepository repository, ClientePersistenceAdapter clienteAdapter) {
        this.repository = repository;
        this.clienteAdapter = clienteAdapter;
    }

    @Override
    public Pedido guardar(Pedido pedido) {
        PedidoEntity entity = toEntity(pedido);
        PedidoEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Pedido> buscarPorId(String id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Pedido> buscarPorCodigo(String codigo) {
        return repository.findByCodigo(codigo).map(this::toDomain);
    }

    @Override
    public List<Pedido> listarTodos() {
        return repository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Pedido> listarPorClienteId(String clienteId) {
        return repository.findByClienteId(clienteId).stream()
                .map(this::toDomain)
                .toList();
    }

    private PedidoEntity toEntity(Pedido domain) {
        PedidoEntity entity = new PedidoEntity(
                domain.getId(),
                domain.getCodigo(),
                clienteAdapter.toEntity(domain.getCliente()),
                domain.getFechaCreacion(),
                domain.getObservaciones(),
                domain.getMontoBruto(),
                domain.getPorcentajeDescuento(),
                domain.getMontoDescuento(),
                domain.getMontoTotal(),
                domain.getEstadoNombre(),
                domain.getMotivoRechazo()
        );

        for (ItemPedido item : domain.getItems()) {
            ItemPedidoEntity itemEntity = new ItemPedidoEntity(
                    item.getProductoId(),
                    item.getNombreProducto(),
                    item.getCantidad(),
                    item.getPrecioUnitario(),
                    item.getSubtotal()
            );
            entity.agregarItem(itemEntity);
        }

        return entity;
    }

    private Pedido toDomain(PedidoEntity entity) {
        Cliente cliente = clienteAdapter.toDomain(entity.getCliente());

        List<ItemPedido> items = entity.getItems().stream()
                .map(i -> new ItemPedido(i.getProductoId(), i.getNombreProducto(), i.getCantidad(), i.getPrecioUnitario()))
                .toList();

        Pedido pedido = new Pedido(
                entity.getId(),
                entity.getCodigo(),
                cliente,
                items,
                entity.getFechaCreacion(),
                entity.getObservaciones()
        );

        pedido.recalcularTotales(entity.getPorcentajeDescuento());
        pedido.setMotivoRechazo(entity.getMotivoRechazo());

        // Reconstitucion del Patron State
        EstadoPedido estado = switch (entity.getEstado().toUpperCase()) {
            case "APROBADO" -> new EstadoAprobado();
            case "RECHAZADO" -> new EstadoRechazado();
            case "COMPLETADO" -> new EstadoCompletado();
            case "CANCELADO" -> new EstadoCancelado();
            default -> new EstadoPendiente();
        };
        pedido.cambiarEstado(estado);

        return pedido;
    }
}
