package com.smartorders;

import com.smartorders.domain.model.Cliente;
import com.smartorders.domain.model.ItemPedido;
import com.smartorders.domain.model.Pedido;
import com.smartorders.infrastructure.builder.PedidoBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias - Patrón Builder (PedidoBuilder)")
class PedidoBuilderTest {

    private Cliente clienteDummy() {
        return new Cliente("CLI-T1", "Pedro Perez", "pedro@mail.com", 2000000.0, null, null);
    }

    @Test
    @DisplayName("Debe construir un pedido válido con parámetros obligatorios y opcionales")
    void testConstruccionExitosa() {
        Cliente cliente = clienteDummy();
        LocalDateTime fecha = LocalDateTime.of(2026, 10, 3, 10, 0);

        Pedido pedido = new PedidoBuilder(cliente)
                .conId("PED-CUSTOM-1")
                .conCodigo("ORD-CUSTOM-99")
                .conFechaCreacion(fecha)
                .conObservaciones("Dejar en portería")
                .conPorcentajeDescuento(0.15)
                .agregarItem("PRD-1", "Teclado Mecánico", 1, 300000.0)
                .agregarItem(new ItemPedido("PRD-2", "Monitor 24 pulg", 1, 700000.0))
                .build();

        assertNotNull(pedido);
        assertEquals("PED-CUSTOM-1", pedido.getId());
        assertEquals("ORD-CUSTOM-99", pedido.getCodigo());
        assertEquals(cliente, pedido.getCliente());
        assertEquals(2, pedido.getItems().size());
        assertEquals(fecha, pedido.getFechaCreacion());
        assertEquals("Dejar en portería", pedido.getObservaciones());

        // Aritmetica defensiva de totales
        assertEquals(1000000.0, pedido.getMontoBruto());
        assertEquals(0.15, pedido.getPorcentajeDescuento());
        assertEquals(150000.0, pedido.getMontoDescuento());
        assertEquals(850000.0, pedido.getMontoTotal());
        assertEquals("PENDIENTE", pedido.getEstadoNombre());
    }

    @Test
    @DisplayName("Debe lanzar excepción si el cliente es nulo")
    void testErrorClienteNulo() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new PedidoBuilder(null)
        );
        assertTrue(ex.getMessage().contains("cliente es obligatorio"));
    }

    @Test
    @DisplayName("Debe lanzar excepción si se intenta construir un pedido sin ningún ítem")
    void testErrorSinItems() {
        PedidoBuilder builder = new PedidoBuilder(clienteDummy())
                .conObservaciones("Pedido vacío");

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                builder::build
        );
        assertTrue(ex.getMessage().contains("al menos un item"));
    }

    @Test
    @DisplayName("Debe soportar agregar lista en lote de ítems")
    void testAgregarListaItems() {
        List<ItemPedido> items = List.of(
                new ItemPedido("P1", "Item A", 2, 50.0),
                new ItemPedido("P2", "Item B", 1, 100.0)
        );

        Pedido pedido = new PedidoBuilder(clienteDummy())
                .conItems(items)
                .build();

        assertEquals(2, pedido.getItems().size());
        assertEquals(200.0, pedido.getMontoBruto());
    }
}
