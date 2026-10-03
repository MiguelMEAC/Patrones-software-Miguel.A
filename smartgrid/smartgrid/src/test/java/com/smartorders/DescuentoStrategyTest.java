package com.smartorders;

import com.smartorders.domain.model.Cliente;
import com.smartorders.domain.model.Compra;
import com.smartorders.domain.model.DatosFiscales;
import com.smartorders.domain.model.Perfil;
import com.smartorders.infrastructure.strategy.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias - Patrón Strategy (Cálculo de Descuentos Comerciales y Subsidios)")
class DescuentoStrategyTest {

    @Test
    @DisplayName("DescuentoVipStrategy: Aplica 15% solo a clientes con más de 10 compras históricas")
    void testDescuentoVip() {
        DescuentoVipStrategy strategy = new DescuentoVipStrategy();
        assertEquals("DESCUENTO_VIP", strategy.getNombreEstrategia());
        assertEquals(0.15, strategy.getPorcentajeDescuento());

        List<Compra> onceCompras = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            onceCompras.add(new Compra("C" + i, LocalDate.now().minusDays(i), 100.0, "desc"));
        }
        Cliente clienteVip = new Cliente("C-VIP", "VIP", "vip@mail.com", 1000.0, null, null, onceCompras, List.of());
        Cliente clienteNoVip = new Cliente("C-NOVIP", "No VIP", "novip@mail.com", 1000.0, null, null, onceCompras.subList(0, 5), List.of());

        assertTrue(strategy.aplica(clienteVip));
        assertFalse(strategy.aplica(clienteNoVip));
        assertEquals(150.0, strategy.calcularMontoDescuento(clienteVip, 1000.0));
        assertEquals(0.0, strategy.calcularMontoDescuento(clienteNoVip, 1000.0));
    }

    @Test
    @DisplayName("DescuentoSubsidioStrategy: Aplica 20% a estratos socioeconómicos menores a 2")
    void testDescuentoSubsidio() {
        DescuentoSubsidioStrategy strategy = new DescuentoSubsidioStrategy();
        assertEquals(0.20, strategy.getPorcentajeDescuento());

        Cliente clienteEstrato1 = new Cliente("C-E1", "Estrato 1", "e1@mail.com", 1000.0, null,
                new DatosFiscales(1, "123", "Regimen Simple"));
        Cliente clienteEstrato3 = new Cliente("C-E3", "Estrato 3", "e3@mail.com", 1000.0, null,
                new DatosFiscales(3, "456", "Comun"));

        assertTrue(strategy.aplica(clienteEstrato1));
        assertFalse(strategy.aplica(clienteEstrato3));
        assertEquals(200.0, strategy.calcularMontoDescuento(clienteEstrato1, 1000.0));
    }

    @Test
    @DisplayName("DescuentoFrecuenteStrategy: Aplica 10% si tiene más de 5 compras en los últimos 365 días")
    void testDescuentoFrecuente() {
        DescuentoFrecuenteStrategy strategy = new DescuentoFrecuenteStrategy();
        assertEquals(0.10, strategy.getPorcentajeDescuento());

        List<Compra> comprasRecientes = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            comprasRecientes.add(new Compra("CR" + i, LocalDate.now().minusDays(i * 10), 100.0, "item"));
        }
        Cliente clienteFrecuente = new Cliente("C-F", "Frecuente", "f@mail.com", 1000.0, null, null, comprasRecientes, List.of());

        List<Compra> comprasAntiguas = List.of(
                new Compra("CA1", LocalDate.now().minusDays(400), 100.0, "antigua"),
                new Compra("CA2", LocalDate.now().minusDays(500), 100.0, "antigua")
        );
        Cliente clienteNoFrecuente = new Cliente("C-NF", "No Frecuente", "nf@mail.com", 1000.0, null, null, comprasAntiguas, List.of());

        assertTrue(strategy.aplica(clienteFrecuente));
        assertFalse(strategy.aplica(clienteNoFrecuente));
        assertEquals(100.0, strategy.calcularMontoDescuento(clienteFrecuente, 1000.0));
    }

    @Test
    @DisplayName("DescuentoSeniorStrategy: Aplica 5% a clientes mayores de 65 años")
    void testDescuentoSenior() {
        DescuentoSeniorStrategy strategy = new DescuentoSeniorStrategy();
        assertEquals(0.05, strategy.getPorcentajeDescuento());

        Cliente clienteMayor = new Cliente("C-SR", "Mayor", "m@mail.com", 1000.0, new Perfil(68, "Pensionado", ""), null);
        Cliente clienteJoven = new Cliente("C-JV", "Joven", "j@mail.com", 1000.0, new Perfil(30, "Ingeniero", ""), null);

        assertTrue(strategy.aplica(clienteMayor));
        assertFalse(strategy.aplica(clienteJoven));
        assertEquals(50.0, strategy.calcularMontoDescuento(clienteMayor, 1000.0));
    }

    @Test
    @DisplayName("DescuentoContext: Selecciona dinámicamente la estrategia de mayor ahorro para el cliente")
    void testDescuentoContextSeleccionOptima() {
        List<DescuentoStrategy> estrategias = List.of(
                new DescuentoVipStrategy(),
                new DescuentoSubsidioStrategy(),
                new DescuentoFrecuenteStrategy(),
                new DescuentoSeniorStrategy(),
                new DescuentoRegularStrategy()
        );
        DescuentoContext context = new DescuentoContext(estrategias);

        // Cliente que califica tanto para Senior (5%) como para Subsidio (20%)
        Cliente clienteMultiBeneficio = new Cliente("C-MB", "Senior vulnerable", "mb@mail.com", 1000.0,
                new Perfil(70, "Hogar", ""), new DatosFiscales(1, "123", "Simple"));

        DescuentoStrategy mejor = context.determinarMejorEstrategia(clienteMultiBeneficio);
        assertEquals("DESCUENTO_SUBSIDIO", mejor.getNombreEstrategia(), "Debe elegir el descuento del 20% frente al del 5%");
        assertEquals(200.0, context.calcularDescuento(clienteMultiBeneficio, 1000.0));
    }
}
