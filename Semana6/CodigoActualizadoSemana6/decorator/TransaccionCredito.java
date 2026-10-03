package com.smartorders.infrastructure.decorator;

import com.smartorders.domain.model.Cliente;
import com.smartorders.domain.ports.out.BuroCreditoPort.ResultadoEvaluacionCredito;

/**
 * Patrón Estructural: Decorator (GoF)
 * Componente Base: Interfaz común para la ejecución de transacciones y evaluación crediticia.
 */
public interface TransaccionCredito {

    ResultadoEvaluacionCredito procesar(Cliente cliente, double monto);
}
