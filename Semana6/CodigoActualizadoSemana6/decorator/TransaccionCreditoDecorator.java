package com.smartorders.infrastructure.decorator;

import com.smartorders.domain.model.Cliente;
import com.smartorders.domain.ports.out.BuroCreditoPort.ResultadoEvaluacionCredito;

/**
 * Decorador Abstracto: Mantiene la referencia al componente envuelto.
 */
public abstract class TransaccionCreditoDecorator implements TransaccionCredito {

    protected final TransaccionCredito transaccionEnvuelta;

    public TransaccionCreditoDecorator(TransaccionCredito transaccionEnvuelta) {
        this.transaccionEnvuelta = transaccionEnvuelta;
    }

    @Override
    public ResultadoEvaluacionCredito procesar(Cliente cliente, double monto) {
        return transaccionEnvuelta.procesar(cliente, monto);
    }
}
