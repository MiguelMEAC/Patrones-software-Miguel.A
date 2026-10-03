package com.smartorders.application.usecase;

import com.smartorders.application.facade.PedidoProcesamientoFacade;
import com.smartorders.domain.model.Pedido;
import com.smartorders.domain.ports.in.FacturarPedidoUseCase;
import com.smartorders.domain.ports.out.PedidoRepositoryPort;
import com.smartorders.infrastructure.factory.Factura;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class FacturacionService implements FacturarPedidoUseCase {

    private final PedidoProcesamientoFacade facade;
    private final PedidoRepositoryPort pedidoRepository;

    public FacturacionService(PedidoProcesamientoFacade facade, PedidoRepositoryPort pedidoRepository) {
        this.facade = facade;
        this.pedidoRepository = pedidoRepository;
    }

    @Override
    public ResultadoFactura generarFactura(String pedidoId, String tipoFactura) {
        Pedido pedido = pedidoRepository.buscarPorId(pedidoId)
                .orElseThrow(() -> new NoSuchElementException("Pedido no encontrado: " + pedidoId));

        Factura factura = facade.procesarFacturacion(pedidoId, tipoFactura);

        String detalle = factura.generarDetalleFactura(pedido);
        double impuestos = factura.calcularImpuestos(pedido);

        return new ResultadoFactura(
                pedido.getCodigo(),
                factura.getTipo(),
                detalle,
                pedido.getMontoTotal(),
                factura.getCodigoFiscal()
        );
    }
}
