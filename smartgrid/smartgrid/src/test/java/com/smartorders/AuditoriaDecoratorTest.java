package com.smartorders;

import com.smartorders.domain.model.Cliente;
import com.smartorders.domain.ports.out.BuroCreditoPort.ResultadoEvaluacionCredito;
import com.smartorders.infrastructure.decorator.AuditoriaTransaccionDecorator;
import com.smartorders.infrastructure.decorator.TransaccionCredito;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Pruebas Unitarias - Patrón Decorator (AuditoriaTransaccionDecorator)")
class AuditoriaDecoratorTest {

    @Test
    @DisplayName("Debe envolver y preservar resultado del componente base mientras ejecuta auditoría")
    void testDecoratorEjecucionTransparente() {
        TransaccionCredito componenteBaseMock = mock(TransaccionCredito.class);
        Cliente cliente = new Cliente("CLI-DEC", "Mario Casas", "mario@mail.com", 1000000.0, null, null);
        ResultadoEvaluacionCredito resultadoMock = new ResultadoEvaluacionCredito(true, 720, "Aprobado por Mock");

        when(componenteBaseMock.procesar(cliente, 200000.0)).thenReturn(resultadoMock);

        AuditoriaTransaccionDecorator decorator = new AuditoriaTransaccionDecorator(componenteBaseMock);
        ResultadoEvaluacionCredito resultadoReal = decorator.procesar(cliente, 200000.0);

        assertNotNull(resultadoReal);
        assertTrue(resultadoReal.aprobado());
        assertEquals(720, resultadoReal.score());
        assertEquals("Aprobado por Mock", resultadoReal.mensaje());

        verify(componenteBaseMock, times(1)).procesar(cliente, 200000.0);
    }
}
