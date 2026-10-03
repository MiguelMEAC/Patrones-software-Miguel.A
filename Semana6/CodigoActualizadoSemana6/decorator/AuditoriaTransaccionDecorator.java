package com.smartorders.infrastructure.decorator;

import com.smartorders.domain.model.Cliente;
import com.smartorders.domain.ports.out.BuroCreditoPort.ResultadoEvaluacionCredito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Decorador Concreto: Añade registro forense de auditoría a la evaluación de crédito
 * sin alterar la lógica de negocio subyacente.
 */
@Component("auditoriaTransaccionDecorator")
@Primary
public class AuditoriaTransaccionDecorator extends TransaccionCreditoDecorator {

    private static final Logger log = LoggerFactory.getLogger(AuditoriaTransaccionDecorator.class);

    public AuditoriaTransaccionDecorator(@Qualifier("transaccionCreditoBase") TransaccionCredito transaccionEnvuelta) {
        super(transaccionEnvuelta);
    }

    @Override
    public ResultadoEvaluacionCredito procesar(Cliente cliente, double monto) {
        LocalDateTime inicio = LocalDateTime.now();
        log.info("[AUDITORIA_TRANSACCION] >>> Iniciando evaluacion de credito | Cliente ID: {} | Nombre: {} | Monto: ${} | Timestamp: {}",
                cliente.getId(), cliente.getNombre(), monto, inicio);

        ResultadoEvaluacionCredito resultado = super.procesar(cliente, monto);

        if (resultado.aprobado()) {
            log.info("[AUDITORIA_TRANSACCION] <<< DICTAMEN: APROBADO | Cliente ID: {} | Score: {} | Detalle: {}",
                    cliente.getId(), resultado.score(), resultado.mensaje());
        } else {
            log.warn("[AUDITORIA_TRANSACCION] <<< DICTAMEN: RECHAZADO | Cliente ID: {} | Score: {} | Motivo: {}",
                    cliente.getId(), resultado.score(), resultado.mensaje());
        }

        return resultado;
    }
}
