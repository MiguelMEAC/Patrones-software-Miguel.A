package com.smartorders.domain.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Entidad de dominio que representa un registro de pago y estado crediticio.
 */
public class Pago {

    private final String id;
    private final LocalDate fecha;
    private final double monto;
    private final boolean pagado;

    public Pago(String id, LocalDate fecha, double monto, boolean pagado) {
        this.id = Objects.requireNonNull(id, "El ID de pago no puede ser nulo");
        this.fecha = Objects.requireNonNull(fecha, "La fecha no puede ser nula");
        this.monto = monto;
        this.pagado = pagado;
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

    public boolean isPagado() {
        return pagado;
    }
}
