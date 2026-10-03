# ADR-001: Adopción de Arquitectura Hexagonal (Puertos y Adaptadores)

## Estado
**Aceptado**

## Contexto
El sistema legacy presentaba un acoplamiento severo entre la capa de presentación, la lógica de cálculo comercial y la persistencia en base de datos. La clase `Pedido` dependía directamente de detalles de infraestructura y cálculos dispersos, dificultando el testing automatizado y la extensibilidad requerida por las directrices institucionales de la UTS.

## Decisión
Se decidió estructurar la solución bajo el paradigma de **Arquitectura Hexagonal (Ports & Adapters)** propuesta por Alistair Cockburn:
1. **Capa de Dominio (`domain`):** Núcleo agnóstico de frameworks que contiene las entidades (`Pedido`, `Cliente`), Value Objects (`Perfil`, `DatosFiscales`, `ItemPedido`), y el patrón de estado (`EstadoPedido`).
2. **Puertos (`ports`):**
   - **Puertos de Entrada (`ports.in`):** Casos de uso (`CrearPedidoUseCase`, `ConsultarPedidoUseCase`, `FacturarPedidoUseCase`).
   - **Puertos de Salida (`ports.out`):** Contratos de infraestructura (`PedidoRepositoryPort`, `ClienteRepositoryPort`, `BuroCreditoPort`, `NotificacionPort`).
3. **Adaptadores (`infrastructure.adapters`):**
   - **Inbound:** Controladores REST (`PedidoController`, `FacturaController`, `SistemaConfigController`).
   - **Outbound:** Persistencia Spring Data JPA (`PedidoPersistenceAdapter`, `ClientePersistenceAdapter`), adaptador de buró (`BuroCreditoAdapter`) y notificaciones (`EmailNotificacionAdapter`).

## Consecuencias
- **Positivas:**
  - El dominio es 100% testeable sin necesidad de iniciar Spring Boot o bases de datos externas.
  - La base de datos es intercambiable (H2 en memoria para pruebas instantáneas y PostgreSQL en producción) sin tocar una sola línea de lógica de negocio.
  - Los 9 patrones de diseño GoF se integran armónicamente en capas naturales.
- **Negativas:**
  - Mayor número de interfaces y clases de mapeo (DTOs <-> Entidades <-> Dominio), compensado ampliamente por la robustez y mantenibilidad.
