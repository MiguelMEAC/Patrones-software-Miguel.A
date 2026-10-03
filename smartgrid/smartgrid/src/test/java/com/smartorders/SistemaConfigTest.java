package com.smartorders;

import com.smartorders.infrastructure.config.SistemaConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias - Patrón Singleton (SistemaConfig)")
class SistemaConfigTest {

    @BeforeEach
    void setUp() {
        SistemaConfig.resetParaPruebas();
    }

    @Test
    @DisplayName("Debe retornar siempre la misma instancia única (Identidad referencial)")
    void testInstanciaUnica() {
        SistemaConfig instancia1 = SistemaConfig.getInstancia();
        SistemaConfig instancia2 = SistemaConfig.getInstancia();

        assertNotNull(instancia1);
        assertSame(instancia1, instancia2, "Ambas referencias deben apuntar exactamente a la misma posición en memoria");
    }

    @Test
    @DisplayName("Debe mantener coherencia en concurrencia multihilo (Thread-Safe)")
    void testConcurrenciaMultihilo() throws InterruptedException {
        int hilos = 50;
        ExecutorService executor = Executors.newFixedThreadPool(hilos);
        CountDownLatch latch = new CountDownLatch(hilos);
        AtomicReference<SistemaConfig> referenciaEsperada = new AtomicReference<>();

        for (int i = 0; i < hilos; i++) {
            executor.submit(() -> {
                SistemaConfig inst = SistemaConfig.getInstancia();
                referenciaEsperada.compareAndSet(null, inst);
                assertSame(referenciaEsperada.get(), inst);
                latch.countDown();
            });
        }

        latch.await();
        executor.shutdown();
    }

    @Test
    @DisplayName("Debe permitir modificar y consultar propiedades globales del sistema")
    void testPropiedadesConfiguracion() {
        SistemaConfig config = SistemaConfig.getInstancia();

        config.setSistemaActivo(false);
        assertFalse(config.isSistemaActivo());

        config.setSistemaActivo(true);
        assertTrue(config.isSistemaActivo());

        config.setTasaIva(0.16);
        assertEquals(0.16, config.getTasaIva());

        config.setLimiteMaximoCreditoPorDefecto(10000000.0);
        assertEquals(10000000.0, config.getLimiteMaximoCreditoPorDefecto());

        config.setPermitirVentaSinStock(true);
        assertTrue(config.isPermitirVentaSinStock());

        config.setNombreSistema("SmartOrders Custom Node");
        assertEquals("SmartOrders Custom Node", config.getNombreSistema());
    }
}
