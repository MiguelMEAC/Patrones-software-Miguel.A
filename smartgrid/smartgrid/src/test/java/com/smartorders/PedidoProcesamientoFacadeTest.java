package com.smartorders;

import com.smartorders.application.facade.PedidoProcesamientoFacade;
import com.smartorders.domain.model.*;
import com.smartorders.domain.ports.in.CrearPedidoUseCase.ItemEntrada;
import com.smartorders.domain.ports.out.BuroCreditoPort.ResultadoEvaluacionCredito;
import com.smartorders.domain.ports.out.ClienteRepositoryPort;
import com.smartorders.domain.ports.out.PedidoRepositoryPort;
import com.smartorders.infrastructure.config.SistemaConfig;
import com.smartorders.infrastructure.decorator.TransaccionCredito;
import com.smartorders.infrastructure.factory.Factura;
import com.smartorders.infrastructure.factory.FacturaEstandarFactory;
import com.smartorders.infrastructure.factory.FacturaFactory;
import com.smartorders.infrastructure.observer.PedidoEventPublisher;
import com.smartorders.infrastructure.strategy.DescuentoContext;
import com.smartorders.infrastructure.strategy.DescuentoRegularStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("Pruebas Unitarias - Patrón Facade (PedidoProcesamientoFacade)")
class PedidoProcesamientoFacadeTest {

    private ClienteRepositoryPort clienteRepository;
    private PedidoRepositoryPort pedidoRepository;
    private TransaccionCredito transaccionCredito;
    private DescuentoContext descuentoContext;
    private PedidoEventPublisher eventPublisher;
    private Map<String, FacturaFactory> fabricas;
    private PedidoProcesamientoFacade facade;

    @BeforeEach
    void setUp() {
        SistemaConfig.resetParaPruebas();
        SistemaConfig.getInstancia().setSistemaActivo(true);

        clienteRepository = mock(ClienteRepositoryPort.class);
        pedidoRepository = mock(PedidoRepositoryPort.class);
        transaccionCredito = mock(TransaccionCredito.class);
        descuentoContext = mock(DescuentoContext.class);
        eventPublisher = mock(PedidoEventPublisher.class);

        fabricas = new HashMap<>();
        fabricas.put("facturaEstandarFactory", new FacturaEstandarFactory());

        facade = new PedidoProcesamientoFacade(
                clienteRepository,
                pedidoRepository,
                transaccionCredito,
                descuentoContext,
                eventPublisher,
                fabricas
        );
    }

    @Test
    @DisplayName("Debe lanzar excepción si el sistema está desactivado por mantenimiento (Singleton)")
    void testSistemaDesactivado() {
        SistemaConfig.getInstancia().setSistemaActivo(false);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> facade.procesarCreacionPedido("CLI-1", List.of(), "Nota")
        );
        assertTrue(ex.getMessage().contains("mantenimiento"));
    }

    @Test
    @DisplayName("Debe orquestar creación, aprobación crediticia y persistencia con éxito")
    void testProcesoCompletoAprobado() {
        Cliente cliente = new Cliente("CLI-1", "Carlos", "carlos@mail.com", 5000000.0, null, null);
        when(clienteRepository.buscarPorId("CLI-1")).thenReturn(Optional.of(cliente));
        when(descuentoContext.determinarMejorEstrategia(any())).thenReturn(new DescuentoRegularStrategy());
        when(transaccionCredito.procesar(eq(cliente), anyDouble()))
                .thenReturn(new ResultadoEvaluacionCredito(true, 750, "Aprobado"));
        when(pedidoRepository.guardar(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<ItemEntrada> items = List.of(
                new ItemEntrada("P1", "Laptop", 1, 2000000.0)
        );

        Pedido resultado = facade.procesarCreacionPedido("CLI-1", items, "Urgente");

        assertNotNull(resultado);
        assertEquals("APROBADO", resultado.getEstadoNombre());
        assertEquals(2000000.0, resultado.getMontoTotal());
        verify(eventPublisher, times(1)).publicarEvento(any());
        verify(pedidoRepository, times(1)).guardar(any());
    }

    @Test
    @DisplayName("Debe rechazar el pedido si la evaluación de crédito falla")
    void testProcesoRechazado() {
        Cliente cliente = new Cliente("CLI-2", "Deudor", "deudor@mail.com", 100000.0, null, null);
        when(clienteRepository.buscarPorId("CLI-2")).thenReturn(Optional.of(cliente));
        when(descuentoContext.determinarMejorEstrategia(any())).thenReturn(new DescuentoRegularStrategy());
        when(transaccionCredito.procesar(eq(cliente), anyDouble()))
                .thenReturn(new ResultadoEvaluacionCredito(false, 450, "Capacidad de pago insuficiente"));
        when(pedidoRepository.guardar(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<ItemEntrada> items = List.of(
                new ItemEntrada("P2", "Nevera", 1, 3000000.0)
        );

        Pedido resultado = facade.procesarCreacionPedido("CLI-2", items, "Observacion");

        assertNotNull(resultado);
        assertEquals("RECHAZADO", resultado.getEstadoNombre());
        assertTrue(resultado.getMotivoRechazo().contains("insuficiente"));
    }

    @Test
    @DisplayName("Debe procesar facturación para un pedido existente (Factory Method)")
    void testFacturacionExitosa() {
        Cliente cliente = new Cliente("CLI-1", "Carlos", "carlos@mail.com", 5000000.0, null, null);
        Pedido pedido = new Pedido("PED-1", "ORD-1", cliente, List.of(new ItemPedido("P1", "Item", 1, 100.0)),
                LocalDateTime.now(), "Nota");
        pedido.aprobar();

        when(pedidoRepository.buscarPorId("PED-1")).thenReturn(Optional.of(pedido));
        when(pedidoRepository.guardar(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Factura factura = facade.procesarFacturacion("PED-1", "ESTANDAR");

        assertNotNull(factura);
        assertEquals("COMPLETADO", pedido.getEstadoNombre(), "Al facturar, el pedido debe pasar a COMPLETADO");
        verify(pedidoRepository, times(1)).guardar(pedido);
        verify(eventPublisher, times(1)).publicarEvento(any());
    }
}
