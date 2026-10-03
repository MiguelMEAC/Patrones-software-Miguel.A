package com.smartorders.infrastructure.decorator;

import com.smartorders.domain.model.Cliente;
import com.smartorders.domain.ports.out.BuroCreditoPort;
import com.smartorders.domain.ports.out.BuroCreditoPort.ResultadoEvaluacionCredito;
import org.springframework.stereotype.Component;

/**
 * Componente Concreto: Implementación base de la transacción de crédito que delega en el puerto de buró.
 */
@Component("transaccionCreditoBase")
public class TransaccionCreditoBase implements TransaccionCredito {

    private final BuroCreditoPort buroCreditoPort;

    public TransaccionCreditoBase(BuroCreditoPort buroCreditoPort) {
        this.buroCreditoPort = buroCreditoPort;
    }

    @Override
    public ResultadoEvaluacionCredito procesar(Cliente cliente, double monto) {
        return buroCreditoPort.evaluarCredito(cliente, monto);
    }
}
