package com.smartorders.infrastructure.adapters.in.web;

import com.smartorders.application.dto.PedidoRequestDTO;
import com.smartorders.application.dto.PedidoResponseDTO;
import com.smartorders.domain.model.ItemPedido;
import com.smartorders.domain.model.Pedido;
import com.smartorders.domain.ports.in.ConsultarPedidoUseCase;
import com.smartorders.domain.ports.in.CrearPedidoUseCase;
import com.smartorders.domain.ports.in.CrearPedidoUseCase.ItemEntrada;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*")
public class PedidoController {

    private final CrearPedidoUseCase crearPedidoUseCase;
    private final ConsultarPedidoUseCase consultarPedidoUseCase;

    public PedidoController(CrearPedidoUseCase crearPedidoUseCase, ConsultarPedidoUseCase consultarPedidoUseCase) {
        this.crearPedidoUseCase = crearPedidoUseCase;
        this.consultarPedidoUseCase = consultarPedidoUseCase;
    }

    @PostMapping
    public ResponseEntity<PedidoResponseDTO> crearPedido(@RequestBody PedidoRequestDTO request) {
        List<ItemEntrada> itemsEntrada = request.items().stream()
                .map(i -> new ItemEntrada(i.productoId(), i.nombreProducto(), i.cantidad(), i.precioUnitario()))
                .toList();

        Pedido pedido = crearPedidoUseCase.crearPedido(request.clienteId(), itemsEntrada, request.observaciones());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponseDTO(pedido));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> obtenerPorId(@PathVariable String id) {
        return consultarPedidoUseCase.obtenerPorId(id)
                .map(this::toResponseDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<PedidoResponseDTO>> listarTodos() {
        List<PedidoResponseDTO> lista = consultarPedidoUseCase.listarTodos().stream()
                .map(this::toResponseDTO)
                .toList();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<PedidoResponseDTO>> listarPorCliente(@PathVariable String clienteId) {
        List<PedidoResponseDTO> lista = consultarPedidoUseCase.listarPorCliente(clienteId).stream()
                .map(this::toResponseDTO)
                .toList();
        return ResponseEntity.ok(lista);
    }

    private PedidoResponseDTO toResponseDTO(Pedido p) {
        List<PedidoResponseDTO.ItemDetalleDTO> itemsDTO = p.getItems().stream()
                .map(i -> new PedidoResponseDTO.ItemDetalleDTO(
                        i.getProductoId(),
                        i.getNombreProducto(),
                        i.getCantidad(),
                        i.getPrecioUnitario(),
                        i.getSubtotal()))
                .toList();

        return new PedidoResponseDTO(
                p.getId(),
                p.getCodigo(),
                p.getCliente().getId(),
                p.getCliente().getNombre(),
                p.getEstadoNombre(),
                p.getMontoBruto(),
                p.getPorcentajeDescuento(),
                p.getMontoDescuento(),
                p.getMontoTotal(),
                p.getObservaciones(),
                p.getMotivoRechazo(),
                p.getFechaCreacion(),
                itemsDTO
        );
    }
}
