# ADR-004: Implementación de Patrones de Comportamiento (Strategy, Observer, State)

## Estado
**Aceptado**

## Contexto
El código inicial suministrado por el usuario presentaba tres problemas críticos de comportamiento:
1. `calcularDescuento()` usaba una cadena de `if` acoplados violando el principio Open/Closed (OCP).
2. Los cambios de estado de un pedido (aprobado, rechazado, cancelado) no notificaban a inventario, cliente ni sistemas de telemetría.
3. El ciclo de vida de un pedido requería validaciones complejas de transiciones válidas e inválidas.

## Decisiones de Diseño

### 1. Patrón Strategy (`DescuentoStrategy`)
- **Problema:** En el código original:
  ```java
  public double calcularDescuento() {
      if (cliente.getHistorialCompras().size() > 10) return 0.15;
      if (cliente.getHistorialCompras().stream()... > 5) return 0.10;
      if (cliente.getPerfil().getEdad() > 65) return 0.05;
      if (cliente.getDatosFiscales().getEstrato() < 2) return 0.20;
      return 0;
  }
  ```
- **Solución:** Se transformó cada regla en una estrategia concreta encapsulada:
  - `DescuentoVipStrategy` (> 10 compras -> 15%)
  - `DescuentoSubsidioStrategy` (Estrato < 2 -> 20%)
  - `DescuentoFrecuenteStrategy` (> 5 compras en 365 días -> 10%)
  - `DescuentoSeniorStrategy` (Edad > 65 -> 5%)
  - `DescuentoRegularStrategy` (0%)
  - `DescuentoContext`: Evalúa dinámicamente el catálogo y otorga el mayor beneficio económico al cliente de forma extensible sin modificar código existente.

### 2. Patrón Observer (`PedidoEventPublisher` / `PedidoObserver`)
- **Problema:** Múltiples subsistemas (inventario, notificaciones, auditoría Prometheus) necesitaban reaccionar ante eventos de pedidos sin acoplar la clase `Pedido`.
- **Solución:** Sujeto `PedidoEventPublisher` que difunde instancias de `PedidoEvent` a los observadores suscritos:
  - `InventarioObserver`: Reserva o libera existencias.
  - `NotificacionClienteObserver`: Notifica por correo.
  - `AuditoriaObserver`: Emite métricas de eventos para Prometheus.

### 3. Patrón State (`EstadoPedido`)
- **Problema:** Controlar el ciclo de vida del pedido (`PENDIENTE`, `APROBADO`, `RECHAZADO`, `COMPLETADO`, `CANCELADO`) evitando transiciones ilegales (ej. completar un pedido pendiente o rechazar uno completado).
- **Solución:** Cada estado es una clase que implementa `EstadoPedido`, encapsulando las reglas de transición y garantizando integridad transaccional.
