package com.smartorders;

import com.smartorders.domain.model.Cliente;
import com.smartorders.domain.model.DatosFiscales;
import com.smartorders.domain.model.ItemPedido;
import com.smartorders.domain.model.Pedido;
import com.smartorders.infrastructure.factory.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias - Patrón Factory Method (Facturación)")
class FacturaFactoryTest {

    private Pedido crearPedidoDummy() {
        Cliente cliente = new Cliente("CLI-1", "Empresa ABC", "contacto@abc.com", 5000000.0,
                null, new DatosFiscales(3, "901.456.789-0", "Responsable de IVA"));

        List<ItemPedido> items = List.of(
                new ItemPedido("PRD-1", "Laptop Dell XPS", 1, 4000000.0),
                new ItemPedido("PRD-2", "Mouse Inalámbrico", 2, 50000.0)
        );

        Pedido pedido = new Pedido("PED-1", "ORD-1001", cliente, items, LocalDateTime.now(), "Entrega prioritaria");
        pedido.recalcularTotales(0.10); // 10% descuento
        return pedido;
    }

    @Test
    @DisplayName("FacturaEstandarFactory debe producir instancia concreta de FacturaEstandar")
    void testFacturaEstandarFactory() {
        FacturaFactory factory = new FacturaEstandarFactory();
        assertEquals("ESTANDAR", factory.getTipoFactory());

        Factura factura = factory.crearFactura();
        assertNotNull(factura);
        assertInstanceOf(FacturaEstandar.class, factura);
        assertEquals("ESTANDAR", factura.getTipo());

        Pedido pedido = crearPedidoDummy();
        String detalle = factory.procesarYEmitir(pedido);
        assertTrue(detalle.contains("FACTURA SIMPLIFICADA"));
        assertTrue(detalle.contains("ORD-1001"));
        assertTrue(factura.calcularImpuestos(pedido) > 0);
        assertNotNull(factura.getCodigoFiscal());
    }

    @Test
    @DisplayName("FacturaElectronicaFiscalFactory debe producir FacturaElectronicaFiscal con CUFE válido")
    void testFacturaElectronicaFiscalFactory() {
        FacturaFactory factory = new FacturaElectronicaFiscalFactory();
        assertEquals("ELECTRONICA_FISCAL", factory.getTipoFactory());

        Factura factura = factory.crearFactura();
        assertNotNull(factura);
        assertInstanceOf(FacturaElectronicaFiscal.class, factura);
        assertEquals("ELECTRONICA_FISCAL", factura.getTipo());

        Pedido pedido = crearPedidoDummy();
        String detalle = factory.procesarYEmitir(pedido);
        assertTrue(detalle.contains("FACTURA ELECTRONICA DIAN"));
        assertTrue(detalle.contains("CUFE-"));
        assertTrue(detalle.contains("901.456.789-0"));
        assertTrue(factura.getCodigoFiscal().startsWith("CUFE-"));
        assertEquals(Math.round(pedido.getMontoTotal() * 0.19 * 100.0) / 100.0, factura.calcularImpuestos(pedido));
    }
}
