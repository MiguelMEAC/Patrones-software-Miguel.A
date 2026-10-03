package com.smartorders.infrastructure.adapter;

import org.springframework.stereotype.Service;

/**
 * Servicio Legacy / Externo de Central de Riesgo (Buro Crediticio).
 * Posee una interfaz y formato de datos propietarios e incompatibles con el dominio actual.
 */
@Service
public class BuroCreditoLegacyService {

    public LegacyScoreResponse consultarCentralRiesgoLegacy(String documentoIdentidad, double montoEndeudamiento, int estratoSocioeconomico) {
        // Simulacion de calculo heuristico de score crediticio legacy (300 a 950 puntos)
        int scoreBase = 650;

        if (estratoSocioeconomico <= 2) {
            scoreBase += 40; // Politica de inclusion financiera
        }

        if (montoEndeudamiento > 2000000.0) {
            scoreBase -= 50;
        }

        boolean esApto = scoreBase >= 600;
        String dictamen = esApto ? "APTO_PARA_CREDITO_COMERCIAL" : "RIESGO_ALTO_RECHAZAR";

        return new LegacyScoreResponse(documentoIdentidad, scoreBase, dictamen);
    }

    public record LegacyScoreResponse(String doc, int puntajePuntual, String dictamenTexto) {}
}
