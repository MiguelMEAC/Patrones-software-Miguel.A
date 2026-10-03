package com.smartorders.application.facade;

import com.smartorders.domain.model.Cliente;
import com.smartorders.domain.model.ItemPedido;
import com.smartorders.domain.model.Pedido;
import com.smartorders.domain.ports.in.CrearPedidoUseCase.ItemEntrada;
import com.smartorders.domain.ports.out.BuroCreditoPort.ResultadoEvaluacionCredito;
import com.smartorders.domain.ports.out.ClienteRepositoryPort;
import com.smartorders.domain.ports.out.PedidoRepositoryPort;
import com.smartorders.infrastructure.builder.PedidoBuilder;
import com.smartorders.infrastructure.config.SistemaConfig;
import com.smartorders.infrastructure.decorator.TransaccionCredito;
import com.smartorders.infrastructure.factory.Factura;
import com.smartorders.infrastructure.factory.FacturaFactory;
import com.smartorders.infrastructure.observer.PedidoEvent;
import com.smartorders.infrastructure.observer.PedidoEventPublisher;
import com.smartorders.infrastructure.strategy.DescuentoContext;
import com.smartorders.infrastructure.strategy.DescuentoStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Patrón Estructural: Facade (GoF)
 * Fachada de Procesamiento Integral de Pedidos.
 * Proporciona una interfaz unificada y de alto nivel que oculta la complejidad
 * del subsistema de crédito,
 * estrategias de descuento, construcción inmutable, observadores, persistencia
 * y facturación.
 */
@Component
public class PedidoProcesamientoFacade {

    private static final Logger log = LoggerFactory.getLogger(PedidoProcesamientoFacade.class);

    private final ClienteRepositoryPort clienteRepository;
    private final PedidoRepositoryPort pedidoRepository;
    private final TransaccionCredito transaccionCredito; // Decorator
    private final DescuentoContext descuentoContext; // Strategy
    private final PedidoEventPublisher eventPublisher; // Observer
    private final Map<String, FacturaFactory> fabricasFactura; // Factory Method

    public PedidoProcesamientoFacade(
            ClienteRepositoryPort clienteRepository,
            PedidoRepositoryPort pedidoRepository,
            TransaccionCredito transaccionCredito,
            DescuentoContext descuentoContext,
            PedidoEventPublisher eventPublisher,
            Map<String, FacturaFactory> fabricasFactura) {
        this.clienteRepository = clienteRepository;
        this.pedidoRepository = pedidoRepository;
        this.transaccionCredito = transaccionCredito;
        this.descuentoContext = descuentoContext;
        this.eventPublisher = eventPublisher;
        this.fabricasFactura = fabricasFactura;
    }

    /**
     * Orquesta el flujo completo de creación y validación de pedido.
     */
    public Pedido procesarCreacionPedido(String clienteId, List<ItemEntrada> itemsEntrada, String observaciones) {
        // 1. Validacion de estado del sistema (Singleton)
        SistemaConfig config = SistemaConfig.getInstancia();
        if (!config.isSistemaActivo()) {
            throw new IllegalStateException("El sistema de pedidos se encuentra actualmente en mantenimiento.");
        }

        // 2. Busqueda de cliente
        Cliente cliente = clienteRepository.buscarPorId(clienteId)
                .orElseThrow(() -> new NoSuchElementException("Cliente no encontrado con ID: " + clienteId));

        if (itemsEntrada == null || itemsEntrada.isEmpty()) {
            throw new IllegalArgumentException("Debe ingresar al menos un producto en el pedido");
        }

        // 3. Mapeo de items
        List<ItemPedido> items = itemsEntrada.stream()
                .map(i -> new ItemPedido(i.productoId(), i.nombreProducto(), i.cantidad(), i.precioUnitario()))
                .toList();

        double montoBrutoPreliminar = items.stream()
                .mapToDouble(ItemPedido::getSubtotal)
                .sum();

        // 4. Seleccion y aplicacion de estrategia de descuento (Strategy)
        DescuentoStrategy estrategia = descuentoContext.determinarMejorEstrategia(cliente);
        double porcentajeDescuento = estrategia.getPorcentajeDescuento();
        log.info("[FACADE] Aplicando estrategia [{}] con {}% de descuento al cliente {}",
                estrategia.getNombreEstrategia(), porcentajeDescuento * 100, cliente.getNombre());

        // 5. Construccion del Pedido (Builder)
        PedidoBuilder builder = new PedidoBuilder(cliente)
                .conItems(items)
                .conObservaciones(observaciones)
                .conPorcentajeDescuento(porcentajeDescuento);

        Pedido pedido = builder.build();

        // 6. Evaluacion de Credito y Auditoria (Decorator + Adapter)
        ResultadoEvaluacionCredito evaluacion = transaccionCredito.procesar(cliente, pedido.getMontoTotal());

        // 7. Transicion de Estado (State)
        if (evaluacion.aprobado()) {
            pedido.aprobar();
            log.info("[FACADE] Pedido {} APROBADO por credito", pedido.getCodigo());
        } else {
            pedido.rechazar(evaluacion.mensaje());
            log.warn("[FACADE] Pedido {} RECHAZADO: {}", pedido.getCodigo(), evaluacion.mensaje());
        }

        // 8. Persistencia
        Pedido guardado = pedidoRepository.guardar(pedido);

        // 9. Emision de Eventos a Observadores (Observer)
        PedidoEvent.TipoEvento tipoEvento = guardado.getEstadoNombre().equals("APROBADO")
                ? PedidoEvent.TipoEvento.PEDIDO_APROBADO
                : PedidoEvent.TipoEvento.PEDIDO_RECHAZADO;

        eventPublisher.publicarEvento(PedidoEvent.of(guardado, tipoEvento, evaluacion.mensaje()));

        return guardado;
    }

    /**
     * Orquesta la facturacion utilizando la fabrica adecuada (Factory Method).
     */
    public Factura procesarFacturacion(String pedidoId, String tipoFactura) {
        Pedido pedido = pedidoRepository.buscarPorId(pedidoId)
                .orElseThrow(() -> new NoSuchElementException("Pedido no encontrado con ID: " + pedidoId));

        String key = "factura" + (tipoFactura != null && tipoFactura.toUpperCase().contains("ELECTRONICA")
                ? "ElectronicaFiscalFactory"
                : "EstandarFactory");

        FacturaFactory factory = fabricasFactura.get(key);
        if (factory == null) {
            factory = fabricasFactura.values().iterator().next();
        }

        Factura factura = factory.crearFactura();

        // Si el pedido estaba aprobado, avanza a completado (State)
        if ("APROBADO".equals(pedido.getEstadoNombre())) {
            pedido.completar();
            pedidoRepository.guardar(pedido);
            eventPublisher.publicarEvento(PedidoEvent.of(pedido, PedidoEvent.TipoEvento.PEDIDO_COMPLETADO,
                    "Factura emitida exitosamente: " + factura.getTipo()));
        }

        return factura;
    }
}
