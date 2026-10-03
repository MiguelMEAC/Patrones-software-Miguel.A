package com.smartorders.infrastructure.adapters.in.web;

import com.smartorders.application.dto.FacturaResponseDTO;
import com.smartorders.domain.ports.in.FacturarPedidoUseCase;
import com.smartorders.domain.ports.in.FacturarPedidoUseCase.ResultadoFactura;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/facturas")
@CrossOrigin(origins = "*")
public class FacturaController {

    private final FacturarPedidoUseCase facturarPedidoUseCase;

    public FacturaController(FacturarPedidoUseCase facturarPedidoUseCase) {
        this.facturarPedidoUseCase = facturarPedidoUseCase;
    }

    @PostMapping("/generar")
    public ResponseEntity<FacturaResponseDTO> generarFactura(
            @RequestParam String pedidoId,
            @RequestParam(name = "tipo", required = false) String tipo,
            @RequestParam(name = "tipoFactura", required = false) String tipoFactura) {

        String tipoFinal = (tipoFactura != null && !tipoFactura.isBlank()) 
                ? tipoFactura 
                : (tipo != null && !tipo.isBlank() ? tipo : "ESTANDAR");

        ResultadoFactura resultado = facturarPedidoUseCase.generarFactura(pedidoId, tipoFinal);

        return ResponseEntity.ok(new FacturaResponseDTO(
                resultado.tipo(),
                resultado.codigoFiscal(),
                Math.round(resultado.total() * 0.19 * 100.0) / 100.0,
                resultado.detalle()
        ));
    }
}
