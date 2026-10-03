package com.smartorders.infrastructure.strategy;

import com.smartorders.domain.model.Cliente;

/**
 * Patrón de Comportamiento: Strategy (GoF)
 * Interfaz que define el contrato común para la familia de algoritmos de cálculo de descuentos comerciales y subsidios.
 * Resuelve la violación de OCP (Open/Closed Principle) eliminando cadenas de if-else acopladas.
 */
public interface DescuentoStrategy {

    String getNombreEstrategia();

    boolean aplica(Cliente cliente);

    double getPorcentajeDescuento();

    default double calcularMontoDescuento(Cliente cliente, double montoPedido) {
        if (!aplica(cliente)) {
            return 0.0;
        }
        return Math.round(montoPedido * getPorcentajeDescuento() * 100.0) / 100.0;
    }
}
