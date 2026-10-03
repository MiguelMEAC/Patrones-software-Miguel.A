package com.smartorders.infrastructure.strategy;

import com.smartorders.domain.model.Cliente;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Estrategia de Cliente Frecuente: Clientes con mas de 5 compras en el ultimo año (365 dias) obtienen 10%.
 * Utiliza java.time.LocalDate determinista y desacoplado de Calendar/Date.
 */
@Component
@Order(3)
public class DescuentoFrecuenteStrategy implements DescuentoStrategy {

    @Override
    public String getNombreEstrategia() {
        return "DESCUENTO_FRECUENTE";
    }

    @Override
    public boolean aplica(Cliente cliente) {
        return cliente != null && cliente.contarComprasEnDias(365) > 5;
    }

    @Override
    public double getPorcentajeDescuento() {
        return 0.10; // 10%
    }
}
