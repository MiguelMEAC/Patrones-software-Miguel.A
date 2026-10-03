package com.smartorders.domain.model;

import com.smartorders.domain.state.EstadoPedido;
import com.smartorders.domain.state.EstadoPendiente;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Entidad Agregada Raiz del dominio: Pedido.
 * Encapsula cliente, items, fecha, calculos monetarios y estado de ciclo de vida (Patron State).
 */
public class Pedido {

    private final String id;
    private final String codigo;
    private final Cliente cliente;
    private final List<ItemPedido> items;
    private final LocalDateTime fechaCreacion;
    private final String observaciones;

    private double montoBruto;
    private double porcentajeDescuento;
    private double montoDescuento;
    private double montoTotal;

    private EstadoPedido estado;
    private String motivoRechazo;

    public Pedido(String id, String codigo, Cliente cliente, List<ItemPedido> items,
                  LocalDateTime fechaCreacion, String observaciones) {
        this.id = Objects.requireNonNull(id, "El ID de pedido es obligatorio");
        this.codigo = Objects.requireNonNull(codigo, "El codigo de pedido es obligatorio");
        this.cliente = Objects.requireNonNull(cliente, "El cliente es obligatorio");
        this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
        this.fechaCreacion = fechaCreacion != null ? fechaCreacion : LocalDateTime.now();
        this.observaciones = observaciones != null ? observaciones : "";
        this.estado = new EstadoPendiente();
        recalcularTotales(0.0);
    }

    public void recalcularTotales(double porcentajeDescuento) {
        this.montoBruto = items.stream()
                .mapToDouble(ItemPedido::getSubtotal)
                .sum();
        this.montoBruto = Math.round(this.montoBruto * 100.0) / 100.0;

        this.porcentajeDescuento = Math.max(0.0, Math.min(1.0, porcentajeDescuento));
        this.montoDescuento = Math.round(this.montoBruto * this.porcentajeDescuento * 100.0) / 100.0;
        this.montoTotal = Math.round((this.montoBruto - this.montoDescuento) * 100.0) / 100.0;
    }

    public void cambiarEstado(EstadoPedido nuevoEstado) {
        this.estado = Objects.requireNonNull(nuevoEstado, "El nuevo estado no puede ser nulo");
    }

    public void aprobar() {
        this.estado.aprobar(this);
    }

    public void rechazar(String motivo) {
        this.estado.rechazar(this, motivo);
    }

    public void completar() {
        this.estado.completar(this);
    }

    public void cancelar() {
        this.estado.cancelar(this);
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public List<ItemPedido> getItems() {
        return Collections.unmodifiableList(items);
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public double getMontoBruto() {
        return montoBruto;
    }

    public double getPorcentajeDescuento() {
        return porcentajeDescuento;
    }

    public double getMontoDescuento() {
        return montoDescuento;
    }

    public double getMontoTotal() {
        return montoTotal;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public String getEstadoNombre() {
        return estado != null ? estado.getNombre() : "DESCONOCIDO";
    }

    public String getMotivoRechazo() {
        return motivoRechazo;
    }

    public void setMotivoRechazo(String motivoRechazo) {
        this.motivoRechazo = motivoRechazo;
    }
}
