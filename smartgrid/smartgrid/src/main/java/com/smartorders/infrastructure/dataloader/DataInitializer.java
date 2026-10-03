package com.smartorders.infrastructure.dataloader;

import com.smartorders.domain.model.*;
import com.smartorders.domain.ports.out.ClienteRepositoryPort;
import com.smartorders.domain.ports.out.PedidoRepositoryPort;
import com.smartorders.infrastructure.builder.PedidoBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private final ClienteRepositoryPort clienteRepository;
    private final PedidoRepositoryPort pedidoRepository;

    public DataInitializer(ClienteRepositoryPort clienteRepository, PedidoRepositoryPort pedidoRepository) {
        this.clienteRepository = clienteRepository;
        this.pedidoRepository = pedidoRepository;
    }

    @Override
    public void run(String... args) {
        if (!clienteRepository.listarTodos().isEmpty()) {
            return;
        }

        log.info("[DATA_INITIALIZER] Precargando clientes de prueba para diferentes perfiles de descuento y credito...");

        // 1. Cliente VIP (> 10 compras) -> Descuento VIP 15%
        List<Compra> comprasVip = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            comprasVip.add(new Compra("C-VIP-" + i, LocalDate.now().minusDays(i * 10), 150000.0, "Compra previa " + i));
        }
        Cliente clienteVip = new Cliente(
                "CLI-VIP", "Carlos Mendoza (VIP)", "carlos.mendoza@email.com", 15000000.0,
                new Perfil(45, "Empresario", "+57 311 000 1111"),
                new DatosFiscales(4, "900.123.456-1", "Comun"),
                comprasVip,
                List.of(new Pago("P-VIP-1", LocalDate.now().minusDays(15), 500000.0, true))
        );
        clienteRepository.guardar(clienteVip);

        // 2. Cliente Subsidio (Estrato 1) -> Descuento Subsidio 20%
        Cliente clienteSubsidio = new Cliente(
                "CLI-SUBSIDIO", "Ana Gomez (Subsidio)", "ana.gomez@email.com", 3000000.0,
                new Perfil(28, "Estudiante", "+57 312 222 3333"),
                new DatosFiscales(1, "1.098.765.432", "Simplificado"),
                List.of(new Compra("C-SUB-1", LocalDate.now().minusDays(60), 80000.0, "Compra basica")),
                List.of(new Pago("P-SUB-1", LocalDate.now().minusDays(30), 80000.0, true))
        );
        clienteRepository.guardar(clienteSubsidio);

        // 3. Cliente Frecuente (> 5 compras en ultimo año) -> Descuento Frecuente 10%
        List<Compra> comprasFrecuente = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            comprasFrecuente.add(new Compra("C-FREC-" + i, LocalDate.now().minusDays(i * 30), 120000.0, "Compra mensual " + i));
        }
        Cliente clienteFrecuente = new Cliente(
                "CLI-FRECUENTE", "Julian Rueda (Frecuente)", "julian.rueda@email.com", 5000000.0,
                new Perfil(35, "Ingeniero", "+57 315 444 5555"),
                new DatosFiscales(3, "88.777.666", "Comun"),
                comprasFrecuente,
                List.of(new Pago("P-FREC-1", LocalDate.now().minusDays(10), 120000.0, true))
        );
        clienteRepository.guardar(clienteFrecuente);

        // 4. Cliente Senior (Edad > 65) -> Descuento Senior 5%
        Cliente clienteSenior = new Cliente(
                "CLI-SENIOR", "Martha Lucia Ortiz (Senior)", "martha.ortiz@email.com", 4000000.0,
                new Perfil(72, "Pensionada", "+57 318 777 8888"),
                new DatosFiscales(3, "28.333.444", "Simplificado"),
                List.of(new Compra("C-SEN-1", LocalDate.now().minusDays(40), 95000.0, "Medicamentos y viveres")),
                List.of(new Pago("P-SEN-1", LocalDate.now().minusDays(20), 95000.0, true))
        );
        clienteRepository.guardar(clienteSenior);

        // 5. Cliente Regular (Sin beneficios especiales) -> Descuento 0%
        Cliente clienteRegular = new Cliente(
                "CLI-REGULAR", "David Torres (Regular)", "david.torres@email.com", 2000000.0,
                new Perfil(29, "Disenador", "+57 320 999 0000"),
                new DatosFiscales(3, "1.095.123.789", "Comun"),
                List.of(new Compra("C-REG-1", LocalDate.now().minusDays(90), 50000.0, "Articulo de oficina")),
                List.of(new Pago("P-REG-1", LocalDate.now().minusDays(45), 50000.0, true))
        );
        clienteRepository.guardar(clienteRegular);

        // 6. Cliente Moroso / Riesgoso (Pagos pendientes sin saldar) -> Rechazo de credito
        Cliente clienteMoroso = new Cliente(
                "CLI-MOROSO", "Hector Salamanca (En Mora)", "hector.salamanca@email.com", 500000.0,
                new Perfil(55, "Comerciante", "+57 300 666 9999"),
                new DatosFiscales(2, "13.888.999", "Comun"),
                List.of(new Compra("C-MOR-1", LocalDate.now().minusDays(180), 2000000.0, "Equipo electronico")),
                List.of(new Pago("P-MOR-1", LocalDate.now().minusDays(120), 2000000.0, false)) // NO PAGADO
        );
        clienteRepository.guardar(clienteMoroso);

        log.info("[DATA_INITIALIZER] 6 Clientes de prueba inicializados exitosamente.");

        // 7. Precarga del Pedido ORD-101 (Aprobado) para pruebas inmediatas de Facturacion (Factory Method)
        Pedido pedido101 = new PedidoBuilder(clienteVip)
                .conId("ORD-101")
                .conCodigo("ORD-101")
                .conObservaciones("Pedido precargado para pruebas de facturacion")
                .agregarItem("PROD-01", "Portátil Dell XPS 15", 1, 2500000.0)
                .agregarItem("PROD-02", "Mouse Inalámbrico", 1, 150000.0)
                .conPorcentajeDescuento(0.15)
                .build();
        pedido101.aprobar();
        pedidoRepository.guardar(pedido101);
        log.info("[DATA_INITIALIZER] Pedido ORD-101 precargado exitosamente para pruebas de facturacion.");
    }
}
