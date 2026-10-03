package com.smartorders;

import com.smartorders.domain.model.Cliente;
import com.smartorders.domain.model.DatosFiscales;
import com.smartorders.domain.model.Pago;
import com.smartorders.domain.ports.out.BuroCreditoPort.ResultadoEvaluacionCredito;
import com.smartorders.infrastructure.adapter.BuroCreditoAdapter;
import com.smartorders.infrastructure.adapter.BuroCreditoLegacyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias - Patrón Adapter (BuroCreditoAdapter)")
class BuroCreditoAdapterTest {

    @Test
    @DisplayName("Debe adaptar satisfactoriamente respuesta positiva del servicio legacy")
    void testEvaluacionAprobada() {
        BuroCreditoLegacyService legacyService = new BuroCreditoLegacyService();
        BuroCreditoAdapter adapter = new BuroCreditoAdapter(legacyService);

        Cliente clienteSolvente = new Cliente(
                "C1", "Laura Restrepo", "laura@mail.com", 5000000.0, null,
                new DatosFiscales(3, "1.098.333.222", "Comun"),
                List.of(),
                List.of(new Pago("P1", LocalDate.now().minusDays(10), 100000.0, true))
        );

        ResultadoEvaluacionCredito resultado = adapter.evaluarCredito(clienteSolvente, 500000.0);

        assertNotNull(resultado);
        assertTrue(resultado.aprobado());
        assertTrue(resultado.score() >= 600);
        assertTrue(resultado.mensaje().contains("aprobado exitosamente"));
    }

    @Test
    @DisplayName("Debe denegar crédito si el cliente presenta mora interna aunque el buró apruebe")
    void testEvaluacionRechazadaPorMoraInterna() {
        BuroCreditoLegacyService legacyService = new BuroCreditoLegacyService();
        BuroCreditoAdapter adapter = new BuroCreditoAdapter(legacyService);

        Cliente clienteConMora = new Cliente(
                "C2", "Manuel Rojas", "manuel@mail.com", 2000000.0, null,
                new DatosFiscales(3, "80.111.222", "Comun"),
                List.of(),
                List.of(new Pago("P2", LocalDate.now().minusDays(90), 800000.0, false)) // NO PAGADO
        );

        ResultadoEvaluacionCredito resultado = adapter.evaluarCredito(clienteConMora, 300000.0);

        assertNotNull(resultado);
        assertFalse(resultado.aprobado(), "Debe denegarse por historial moroso");
        assertTrue(resultado.mensaje().contains("denegado"));
    }
}
