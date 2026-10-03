package com.smartorders.infrastructure.strategy;

import com.smartorders.domain.model.Cliente;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Estrategia de Subsidio Social: Clientes con estrato menor a 2 obtienen 20% de descuento.
 */
@Component
@Order(2)
public class DescuentoSubsidioStrategy implements DescuentoStrategy {

    @Override
    public String getNombreEstrategia() {
        return "DESCUENTO_SUBSIDIO";
    }

    @Override
    public boolean aplica(Cliente cliente) {
        return cliente != null && cliente.getDatosFiscales().getEstrato() < 2;
    }

    @Override
    public double getPorcentajeDescuento() {
        return 0.20; // 20%
    }
}
