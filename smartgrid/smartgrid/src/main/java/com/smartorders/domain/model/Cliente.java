package com.smartorders.domain.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Entidad principal de Cliente en el Dominio.
 */
public class Cliente {

    private final String id;
    private final String nombre;
    private final String email;
    private final double limiteCredito;
    private final List<Compra> historialCompras;
    private final Perfil perfil;
    private final DatosFiscales datosFiscales;
    private final List<Pago> historialPagos;

    public Cliente(String id, String nombre, String email, double limiteCredito,
                   Perfil perfil, DatosFiscales datosFiscales) {
        this(id, nombre, email, limiteCredito, perfil, datosFiscales, new ArrayList<>(), new ArrayList<>());
    }

    public Cliente(String id, String nombre, String email, double limiteCredito,
                   Perfil perfil, DatosFiscales datosFiscales,
                   List<Compra> historialCompras, List<Pago> historialPagos) {
        this.id = Objects.requireNonNull(id, "El ID de cliente es obligatorio");
        this.nombre = Objects.requireNonNull(nombre, "El nombre de cliente es obligatorio");
        this.email = email != null ? email : "";
        this.limiteCredito = Math.max(0.0, limiteCredito);
        this.perfil = perfil != null ? perfil : new Perfil(30, "Indefinida", "");
        this.datosFiscales = datosFiscales != null ? datosFiscales : new DatosFiscales(3, "N/A", "Comun");
        this.historialCompras = historialCompras != null ? new ArrayList<>(historialCompras) : new ArrayList<>();
        this.historialPagos = historialPagos != null ? new ArrayList<>(historialPagos) : new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public double getLimiteCredito() {
        return limiteCredito;
    }

    public List<Compra> getHistorialCompras() {
        return Collections.unmodifiableList(historialCompras);
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public DatosFiscales getDatosFiscales() {
        return datosFiscales;
    }

    public List<Pago> getHistorialPagos() {
        return Collections.unmodifiableList(historialPagos);
    }

    public void agregarCompra(Compra compra) {
        if (compra != null) {
            this.historialCompras.add(compra);
        }
    }

    public void agregarPago(Pago pago) {
        if (pago != null) {
            this.historialPagos.add(pago);
        }
    }

    /**
     * Metodo de negocio: Cuenta compras realizadas en los ultimos N dias.
     */
    public long contarComprasEnDias(int dias) {
        LocalDate fechaLimite = LocalDate.now().minusDays(dias);
        return historialCompras.stream()
                .filter(c -> c.getFecha().isAfter(fechaLimite))
                .count();
    }

    /**
     * Metodo de negocio: Valida si todos los pagos previos estan al dia y dentro del limite de credito.
     */
    public boolean validarHistorialCrediticioInterno() {
        if (historialPagos.isEmpty()) {
            return true; // Cliente nuevo con limite asignado
        }
        return historialPagos.stream()
                .allMatch(p -> p.isPagado() && p.getMonto() <= this.limiteCredito);
    }
}
