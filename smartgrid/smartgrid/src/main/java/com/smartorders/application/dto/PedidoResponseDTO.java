package com.smartorders.application.dto;

import java.time.LocalDateTime;
import java.util.List;

public record PedidoResponseDTO(
        String id,
        String codigo,
        String clienteId,
        String clienteNombre,
        String estado,
        double montoBruto,
        double porcentajeDescuento,
        double montoDescuento,
        double montoTotal,
        String observaciones,
        String motivoRechazo,
        LocalDateTime fechaCreacion,
        List<ItemDetalleDTO> items
) {
    public record ItemDetalleDTO(
            String productoId,
            String nombreProducto,
            int cantidad,
            double precioUnitario,
            double subtotal
    ) {}
}
