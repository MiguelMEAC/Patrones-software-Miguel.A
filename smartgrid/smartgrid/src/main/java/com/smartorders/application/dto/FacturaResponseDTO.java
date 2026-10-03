package com.smartorders.application.dto;

public record FacturaResponseDTO(
        String tipo,
        String codigoFiscal,
        double impuestos,
        String detalleFactura
) {}
