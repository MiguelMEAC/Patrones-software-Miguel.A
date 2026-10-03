# ADR-003: Implementación de Patrones Estructurales (Adapter, Decorator, Facade)

## Estado
**Aceptado**

## Contexto
El subsistema de pedidos requería integrar servicios externos legados de scoring crediticio, añadir capas no intrusivas de auditoría forense para transacciones financieras y proveer una interfaz simple y unificada para los controladores REST.

## Decisiones de Diseño

### 1. Patrón Adapter (`BuroCreditoAdapter`)
- **Problema:** El proveedor de crédito opera con contratos antiguos (`consultarCentralRiesgoLegacy`) que reciben documentos crudos y devuelven estructuras no tipadas incompatibles con nuestro puerto `BuroCreditoPort`.
- **Solución:** `BuroCreditoAdapter` implementa `BuroCreditoPort` y traduce las peticiones y respuestas hacia/desde `BuroCreditoLegacyService`, protegiendo el núcleo de negocio de cambios del proveedor.

### 2. Patrón Decorator (`AuditoriaTransaccionDecorator`)
- **Problema:** Se requería registrar auditoría estructurada (timestamp, score, veredicto) de cada evaluación crediticia sin modificar el código base de la transacción ni contaminar el adaptador.
- **Solución:** Se implementó `TransaccionCreditoDecorator` y su especialización `AuditoriaTransaccionDecorator`, la cual intercepta y audita las llamadas de forma totalmente transparente mediante inyección de dependencias (`@Primary`).

### 3. Patrón Facade (`PedidoProcesamientoFacade`)
- **Problema:** Crear un pedido involucra orquestar 6 subsistemas: validar estado del sistema, buscar cliente, evaluar crédito, aplicar estrategia de descuento, construir el pedido inmutable, persistir y emitir eventos. Exponer esto a los controladores generaba acoplamiento severo.
- **Solución:** `PedidoProcesamientoFacade` encapsula toda esta complejidad en un único punto de entrada de alto nivel: `procesarCreacionPedido(...)`.

## Patrones Descartados
- **Bridge:** Descartado porque no existen dos jerarquías ortogonales que varíen independientemente.
- **Composite:** Descartado porque los pedidos y clientes no forman jerarquías de árbol parte-todo recursivas.
