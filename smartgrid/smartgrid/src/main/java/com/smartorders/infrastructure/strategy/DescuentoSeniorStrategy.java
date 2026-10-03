package com.smartorders.infrastructure.strategy;

import com.smartorders.domain.model.Cliente;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Estrategia Senior: Clientes mayores de 65 años obtienen 5% de descuento.
 */
@Component
@Order(4)
public class DescuentoSeniorStrategy implements DescuentoStrategy {

    @Override
    public String getNombreEstrategia() {
        return "DESCUENTO_SENIOR";
    }

    @Override
    public boolean aplica(Cliente cliente) {
        return cliente != null && cliente.getPerfil().getEdad() > 65;
    }

    @Override
    public double getPorcentajeDescuento() {
        return 0.05; // 5%
    }
}
