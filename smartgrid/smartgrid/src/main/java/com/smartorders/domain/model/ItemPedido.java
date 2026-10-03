package com.smartorders.domain.model;

import java.util.Objects;

/**
 * Entidad de dominio que representa una linea de detalle o item dentro de un pedido.
 */
public class ItemPedido {

    private final String productoId;
    private final String nombreProducto;
    private final int cantidad;
    private final double precioUnitario;

    public ItemPedido(String productoId, String nombreProducto, int cantidad, double precioUnitario) {
        this.productoId = Objects.requireNonNull(productoId, "El productoId es obligatorio");
        this.nombreProducto = Objects.requireNonNull(nombreProducto, "El nombre del producto es obligatorio");
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }
        if (precioUnitario < 0) {
            throw new IllegalArgumentException("El precio unitario no puede ser negativo");
        }
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public String getProductoId() {
        return productoId;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public double getSubtotal() {
        return Math.round(cantidad * precioUnitario * 100.0) / 100.0;
    }
}
