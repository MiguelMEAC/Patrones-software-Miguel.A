package com.smartorders.infrastructure.builder;

import com.smartorders.domain.model.Cliente;
import com.smartorders.domain.model.ItemPedido;
import com.smartorders.domain.model.Pedido;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Patrón Creacional: Builder (GoF)
 * Permite la construcción fluida, inmutable y segura de pedidos complejos.
 * Diferencia parámetros obligatorios de opcionales y valida invariantes de negocio antes de instanciar.
 */
public class PedidoBuilder {

    private final Cliente cliente;
    private final List<ItemPedido> items = new ArrayList<>();
    private String id;
    private String codigo;
    private LocalDateTime fechaCreacion;
    private String observaciones;
    private double porcentajeDescuento = 0.0;

    /**
     * El constructor del Builder exige los datos estrictamente obligatorios.
     */
    public PedidoBuilder(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente es obligatorio para construir un pedido");
        }
        this.cliente = cliente;
        this.id = UUID.randomUUID().toString();
        this.codigo = "ORD-" + System.currentTimeMillis();
        this.fechaCreacion = LocalDateTime.now();
        this.observaciones = "Sin observaciones";
    }

    public PedidoBuilder conId(String id) {
        if (id != null && !id.isBlank()) {
            this.id = id;
        }
        return this;
    }

    public PedidoBuilder conCodigo(String codigo) {
        if (codigo != null && !codigo.isBlank()) {
            this.codigo = codigo;
        }
        return this;
    }

    public PedidoBuilder agregarItem(ItemPedido item) {
        if (item != null) {
            this.items.add(item);
        }
        return this;
    }

    public PedidoBuilder agregarItem(String productoId, String nombreProducto, int cantidad, double precioUnitario) {
        return agregarItem(new ItemPedido(productoId, nombreProducto, cantidad, precioUnitario));
    }

    public PedidoBuilder conItems(List<ItemPedido> items) {
        if (items != null) {
            this.items.addAll(items);
        }
        return this;
    }

    public PedidoBuilder conFechaCreacion(LocalDateTime fechaCreacion) {
        if (fechaCreacion != null) {
            this.fechaCreacion = fechaCreacion;
        }
        return this;
    }

    public PedidoBuilder conObservaciones(String observaciones) {
        if (observaciones != null) {
            this.observaciones = observaciones;
        }
        return this;
    }

    public PedidoBuilder conPorcentajeDescuento(double porcentajeDescuento) {
        this.porcentajeDescuento = porcentajeDescuento;
        return this;
    }

    /**
     * Construye la instancia definitiva de Pedido garantizando la integridad de invariantes.
     */
    public Pedido build() {
        if (items.isEmpty()) {
            throw new IllegalStateException("Un pedido no puede ser construido sin al menos un item");
        }

        Pedido pedido = new Pedido(this.id, this.codigo, this.cliente, this.items, this.fechaCreacion, this.observaciones);
        pedido.recalcularTotales(this.porcentajeDescuento);
        return pedido;
    }
}
