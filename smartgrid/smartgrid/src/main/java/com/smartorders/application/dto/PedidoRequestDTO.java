package com.smartorders.application.dto;

import java.util.List;

public record PedidoRequestDTO(
        String clienteId,
        String observaciones,
        List<ItemDTO> items
) {
    public record ItemDTO(
            String productoId,
            String nombreProducto,
            int cantidad,
            double precioUnitario
    ) {}
}
