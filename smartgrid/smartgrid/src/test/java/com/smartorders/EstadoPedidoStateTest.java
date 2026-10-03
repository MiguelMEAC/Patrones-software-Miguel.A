package com.smartorders;

import com.smartorders.domain.model.Cliente;
import com.smartorders.domain.model.ItemPedido;
import com.smartorders.domain.model.Pedido;
import com.smartorders.domain.state.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias - Patrón State (Ciclo de Vida de Pedido)")
class EstadoPedidoStateTest {

    private Pedido crearPedidoEnEstado(EstadoPedido estadoInicial) {
        Cliente cliente = new Cliente("C-1", "Carlos", "c@mail.com", 1000.0, null, null);
        Pedido pedido = new Pedido("P-1", "ORD-1", cliente, List.of(new ItemPedido("X", "Item", 1, 100.0)),
                LocalDateTime.now(), "Nota");
        pedido.cambiarEstado(estadoInicial);
        return pedido;
    }

    @Test
    @DisplayName("Transición normal: PENDIENTE -> APROBADO -> COMPLETADO")
    void testFlujoCompletoExitoso() {
        Pedido pedido = crearPedidoEnEstado(new EstadoPendiente());
        assertEquals("PENDIENTE", pedido.getEstadoNombre());

        pedido.aprobar();
        assertEquals("APROBADO", pedido.getEstadoNombre());

        pedido.completar();
        assertEquals("COMPLETADO", pedido.getEstadoNombre());
    }

    @Test
    @DisplayName("Transición de rechazo: PENDIENTE -> RECHAZADO")
    void testFlujoRechazo() {
        Pedido pedido = crearPedidoEnEstado(new EstadoPendiente());
        pedido.rechazar("Score insuficiente");

        assertEquals("RECHAZADO", pedido.getEstadoNombre());
        assertEquals("Score insuficiente", pedido.getMotivoRechazo());

        // Un pedido rechazado no puede completarse
        assertThrows(IllegalStateException.class, pedido::completar);
    }

    @Test
    @DisplayName("No se puede completar directamente un pedido en estado PENDIENTE")
    void testErrorCompletarPendiente() {
        Pedido pedido = crearPedidoEnEstado(new EstadoPendiente());
        assertThrows(IllegalStateException.class, pedido::completar);
    }

    @Test
    @DisplayName("No se puede rechazar un pedido que ya fue previamente APROBADO")
    void testErrorRechazarAprobado() {
        Pedido pedido = crearPedidoEnEstado(new EstadoAprobado());
        assertThrows(IllegalStateException.class, () -> pedido.rechazar("Error tardío"));
    }

    @Test
    @DisplayName("Cancelación de pedido desde PENDIENTE y APROBADO")
    void testCancelacion() {
        Pedido pedido1 = crearPedidoEnEstado(new EstadoPendiente());
        pedido1.cancelar();
        assertEquals("CANCELADO", pedido1.getEstadoNombre());

        Pedido pedido2 = crearPedidoEnEstado(new EstadoAprobado());
        pedido2.cancelar();
        assertEquals("CANCELADO", pedido2.getEstadoNombre());

        assertThrows(IllegalStateException.class, pedido2::aprobar);
    }
}
