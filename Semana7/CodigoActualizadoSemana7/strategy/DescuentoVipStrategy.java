package com.smartorders.infrastructure.strategy;

import com.smartorders.domain.model.Cliente;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Estrategia de Descuento VIP: Clientes con mas de 10 compras historicas obtienen 15% de descuento.
 */
@Component
@Order(1)
public class DescuentoVipStrategy implements DescuentoStrategy {

    @Override
    public String getNombreEstrategia() {
        return "DESCUENTO_VIP";
    }

    @Override
    public boolean aplica(Cliente cliente) {
        return cliente != null && cliente.getHistorialCompras().size() > 10;
    }

    @Override
    public double getPorcentajeDescuento() {
        return 0.15; // 15%
    }
}
