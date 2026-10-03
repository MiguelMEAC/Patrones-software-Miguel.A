package com.smartorders.domain.ports.out;

import com.smartorders.domain.model.Cliente;

/**
 * Puerto de salida para evaluar la solvencia crediticia del cliente ante centrales de riesgo.
 */
public interface BuroCreditoPort {

    ResultadoEvaluacionCredito evaluarCredito(Cliente cliente, double montoPedido);

    record ResultadoEvaluacionCredito(boolean aprobado, int score, String mensaje) {}
}
