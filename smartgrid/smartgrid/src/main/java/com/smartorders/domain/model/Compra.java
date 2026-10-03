package com.smartorders.domain.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Entidad de dominio que representa una compra historica realizada por un cliente.
 */
public class Compra {

    private final String id;
    private final LocalDate fecha;
    private final double monto;
    private final String descripcion;

    public Compra(String id, LocalDate fecha, double monto, String descripcion) {
        this.id = Objects.requireNonNull(id, "El ID de compra no puede ser nulo");
        this.fecha = Objects.requireNonNull(fecha, "La fecha no puede ser nula");
        this.monto = monto;
        this.descripcion = descripcion != null ? descripcion : "";
    }

    public String getId() {
        return id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public double getMonto() {
        return monto;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
