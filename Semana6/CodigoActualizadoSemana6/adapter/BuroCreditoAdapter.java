package com.smartorders.infrastructure.adapter;

import com.smartorders.domain.model.Cliente;
import com.smartorders.domain.ports.out.BuroCreditoPort;
import org.springframework.stereotype.Component;

/**
 * Patrón Estructural: Adapter (GoF)
 * Adapta el servicio externo BuroCreditoLegacyService al puerto del dominio BuroCreditoPort.
 * Desacopla completamente las entidades y casos de uso de los formatos propietarios del proveedor de crédito.
 */
@Component
public class BuroCreditoAdapter implements BuroCreditoPort {

    private final BuroCreditoLegacyService legacyService;

    public BuroCreditoAdapter(BuroCreditoLegacyService legacyService) {
        this.legacyService = legacyService;
    }

    @Override
    public ResultadoEvaluacionCredito evaluarCredito(Cliente cliente, double montoPedido) {
        // Extraccion y transformacion de parametros requeridos por el servicio legacy
        String documento = cliente.getDatosFiscales().getNitORut();
        int estrato = cliente.getDatosFiscales().getEstrato();

        // Llamada adaptada
        BuroCreditoLegacyService.LegacyScoreResponse response =
                legacyService.consultarCentralRiesgoLegacy(documento, montoPedido, estrato);

        // Mapeo defensivo al contrato del dominio
        boolean aprobado = "APTO_PARA_CREDITO_COMERCIAL".equalsIgnoreCase(response.dictamenTexto())
                && cliente.validarHistorialCrediticioInterno();

        String mensaje = aprobado
                ? "Credito aprobado exitosamente con Score Central: " + response.puntajePuntual()
                : "Credito denegado por politica de riesgo o mora previa. Score: " + response.puntajePuntual();

        return new ResultadoEvaluacionCredito(aprobado, response.puntajePuntual(), mensaje);
    }
}
