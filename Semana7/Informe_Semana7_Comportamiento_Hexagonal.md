# 📑 INFORME TÉCNICO - SEMANA 7: PATRONES DE COMPORTAMIENTO & ARQUITECTURA HEXAGONAL
## Proyecto: SmartOrders Enterprise
### Patrones Implementados: Strategy, Observer, State, Facade + Integración Hexagonal

---

### 1. Patrón Strategy (Resolución Definitiva del Código Legacy)
El método inicial con múltiples `if-else` acoplados fue completamente eliminado y sustituido por estrategias polimórficas independientes:
- `DescuentoVipStrategy` (15% si compras > 10)
- `DescuentoSubsidioStrategy` (20% si estrato < 2)
- `DescuentoFrecuenteStrategy` (10% si > 5 compras en 365 días)
- `DescuentoSeniorStrategy` (5% si edad > 65)
- `DescuentoRegularStrategy` (0%)

El contexto `DescuentoContext` analiza la lista inyectada por Spring y selecciona automáticamente la regla que genere el mayor ahorro para el cliente.

---

### 2. Patrón Observer (Desacoplamiento de Eventos)
`PedidoEventPublisher` difunde eventos de cambio de estado a tres suscriptores autónomos:
1. `InventarioObserver`: Reserva, confirma o libera existencias.
2. `NotificacionClienteObserver`: Envía correos informativos al cliente.
3. `AuditoriaObserver`: Expone métricas de telemetría hacia Prometheus.

---

### 3. Patrón State (Transiciones Deterministas de Pedido)
El ciclo de vida del pedido es gobernado por estados concretos:
`PENDIENTE` $\rightarrow$ `APROBADO` $\rightarrow$ `COMPLETADO` (o `RECHAZADO` / `CANCELADO`).
Cualquier transición ilegal genera `IllegalStateException` protegiendo las reglas del negocio.

---

### 4. Patrón Facade & Arquitectura Hexagonal
`PedidoProcesamientoFacade` unifica y oculta toda esta orquestación ante los controladores REST de la infraestructura.
El resultado es un sistema desacoplado, con **82% de cobertura en JaCoCo**, pipeline de CI/CD automatizado y observabilidad en tiempo real.
