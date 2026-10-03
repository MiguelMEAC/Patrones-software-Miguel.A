package com.smartorders.infrastructure.strategy;

import com.smartorders.domain.model.Cliente;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Estrategia Regular: Sin descuento adicional.
 */
@Component
@Order(99)
public class DescuentoRegularStrategy implements DescuentoStrategy {

    @Override
    public String getNombreEstrategia() {
        return "DESCUENTO_REGULAR";
    }

    @Override
    public boolean aplica(Cliente cliente) {
        return true;
    }

    @Override
    public double getPorcentajeDescuento() {
        return 0.0;
    }
}
