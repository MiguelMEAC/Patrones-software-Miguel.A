package com.smartorders.infrastructure.strategy;

import com.smartorders.domain.model.Cliente;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

/**
 * Contexto del Patrón Strategy:
 * Administra el catálogo de estrategias de descuento y selecciona la óptima (mayor beneficio para el cliente o por prioridad).
 */
@Component
public class DescuentoContext {

    private final List<DescuentoStrategy> estrategias;

    public DescuentoContext(List<DescuentoStrategy> estrategias) {
        this.estrategias = estrategias != null ? estrategias : List.of(new DescuentoRegularStrategy());
    }

    /**
     * Determina la mejor estrategia aplicable para el cliente evaluando el mayor porcentaje de ahorro.
     */
    public DescuentoStrategy determinarMejorEstrategia(Cliente cliente) {
        return estrategias.stream()
                .filter(e -> e.aplica(cliente))
                .max(Comparator.comparingDouble(DescuentoStrategy::getPorcentajeDescuento))
                .orElse(new DescuentoRegularStrategy());
    }

    public double calcularDescuento(Cliente cliente, double montoBruto) {
        DescuentoStrategy estrategia = determinarMejorEstrategia(cliente);
        return estrategia.calcularMontoDescuento(cliente, montoBruto);
    }
}
