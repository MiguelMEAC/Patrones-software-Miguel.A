package com.smartorders;

import com.smartorders.domain.model.Cliente;
import com.smartorders.domain.model.ItemPedido;
import com.smartorders.domain.model.Pedido;
import com.smartorders.infrastructure.observer.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias - Patrón Observer (Eventos de Dominio y Despacho)")
class PedidoObserverTest {

    @Test
    @DisplayName("Debe registrar observadores y notificar eventos en broadcast")
    void testPublicacionEventos() {
        List<PedidoEvent> eventosRecibidos = new ArrayList<>();

        PedidoObserver observerSpy = new PedidoObserver() {
            @Override
            public String getNombreObserver() {
                return "SPY_OBSERVER";
            }

            @Override
            public void onPedidoEvent(PedidoEvent event) {
                eventosRecibidos.add(event);
            }
        };

        PedidoEventPublisher publisher = new PedidoEventPublisher(new ArrayList<>());
        publisher.suscribir(observerSpy);
        publisher.suscribir(new InventarioObserver());
        publisher.suscribir(new NotificacionClienteObserver());
        publisher.suscribir(new AuditoriaObserver());

        assertTrue(publisher.getObservadores().contains(observerSpy));

        Cliente cliente = new Cliente("C-1", "Ana", "ana@mail.com", 1000.0, null, null);
        Pedido pedido = new Pedido("P-1", "ORD-1", cliente, List.of(new ItemPedido("X", "Item", 1, 100.0)),
                LocalDateTime.now(), "Prueba");

        PedidoEvent event = PedidoEvent.of(pedido, PedidoEvent.TipoEvento.PEDIDO_APROBADO, "Aprobado por crédito");
        publisher.publicarEvento(event);

        assertEquals(1, eventosRecibidos.size());
        assertEquals(PedidoEvent.TipoEvento.PEDIDO_APROBADO, eventosRecibidos.get(0).tipoEvento());
        assertEquals("ORD-1", eventosRecibidos.get(0).pedido().getCodigo());

        // Desuscribir
        publisher.desuscribir(observerSpy);
        assertFalse(publisher.getObservadores().contains(observerSpy));
    }
}
