package com.smartorders.application.dto;

public record EstadoSistemaDTO(
        String nombreSistema,
        boolean activo,
        double tasaIva,
        double limiteMaximoCredito,
        boolean permitirVentaSinStock
) {}
