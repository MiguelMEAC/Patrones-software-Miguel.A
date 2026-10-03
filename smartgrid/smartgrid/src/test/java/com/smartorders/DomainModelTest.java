package com.smartorders;

import com.smartorders.domain.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias - Entidades y Value Objects del Dominio")
class DomainModelTest {

    @Test
    @DisplayName("Cliente: Métodos de negocio y colecciones inmutables")
    void testClienteDominio() {
        Perfil perfil = new Perfil(32, "Docente", "+57 300 123 4567");
        DatosFiscales datosFiscales = new DatosFiscales(2, "900.888.777-1", "Común");

        Cliente cliente = new Cliente("CLI-1", "Juan Perez", "juan@mail.com", 3000000.0, perfil, datosFiscales);

        assertEquals("CLI-1", cliente.getId());
        assertEquals("Juan Perez", cliente.getNombre());
        assertEquals("juan@mail.com", cliente.getEmail());
        assertEquals(3000000.0, cliente.getLimiteCredito());
        assertEquals(32, cliente.getPerfil().getEdad());
        assertEquals(2, cliente.getDatosFiscales().getEstrato());
        assertTrue(cliente.getHistorialCompras().isEmpty());
        assertTrue(cliente.validarHistorialCrediticioInterno());

        cliente.agregarCompra(new Compra("CMP-1", LocalDate.now().minusDays(10), 50000.0, "Libros"));
        assertEquals(1, cliente.getHistorialCompras().size());
        assertEquals(1, cliente.contarComprasEnDias(30));
        assertEquals(0, cliente.contarComprasEnDias(5));

        cliente.agregarPago(new Pago("PAG-1", LocalDate.now().minusDays(5), 50000.0, true));
        assertTrue(cliente.validarHistorialCrediticioInterno());

        // Intento de mutar colecciones inmutables
        assertThrows(UnsupportedOperationException.class, () -> cliente.getHistorialCompras().clear());
    }

    @Test
    @DisplayName("ItemPedido: Validación defensiva de cantidades y precios")
    void testItemPedidoValidacion() {
        ItemPedido item = new ItemPedido("PRD-1", "Cafe Especial", 3, 25000.0);
        assertEquals("PRD-1", item.getProductoId());
        assertEquals("Cafe Especial", item.getNombreProducto());
        assertEquals(3, item.getCantidad());
        assertEquals(25000.0, item.getPrecioUnitario());
        assertEquals(75000.0, item.getSubtotal());

        assertThrows(IllegalArgumentException.class, () -> new ItemPedido("P", "N", 0, 10.0));
        assertThrows(IllegalArgumentException.class, () -> new ItemPedido("P", "N", 1, -5.0));
    }
}
