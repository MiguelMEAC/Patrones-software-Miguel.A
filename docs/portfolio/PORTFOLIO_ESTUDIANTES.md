# 👨‍💻 PORTFOLIO TÉCNICO INDIVIDUAL DE INGENIERÍA
## Asignatura: Patrones de Diseño de Software - Unidades Tecnológicas de Santander (UTS)
## Docente: Eliecer Montero Ojeda Ed.D
## Autor: Miguel Eduardo Ardila Cossio (`meac@uts.edu.co`)
### Proyecto: SmartOrders Enterprise

---

### 1. Ficha del Proyecto
- **Nombre:** SmartOrders Enterprise
- **Autor / Ingeniero Líder:** Miguel Eduardo Ardila Cossio
- **Dominio:** Sistema Inteligente de Gestión de Pedidos, Evaluación Crediticia y Facturación Dinámica.
- **Tecnologías:** Java 21, Spring Boot 3.3.4, Spring Data JPA, PostgreSQL / H2, JaCoCo (82% Cobertura), JUnit 5, Mockito, Micrometer Prometheus, GitHub Actions CI/CD.
- **Patrones GoF Implementados:** 9 Patrones (Singleton, Factory Method, Builder, Adapter, Decorator, Facade, Strategy, Observer, State).

---

### 2. Contribuciones Técnicas y Matriz de Responsabilidades

| Rol / Especialidad | Módulos y Patrones Desarrollados | Entregables Clave | Evidencia en Código |
| :--- | :--- | :--- | :--- |
| **Arquitectura de Software & Core** | - Arquitectura Hexagonal (Puertos y Adaptadores)<br>- Patrón Singleton (`SistemaConfig`)<br>- Patrón Facade (`PedidoProcesamientoFacade`) | ADR-001, ADR-002, Pipeline CI/CD | `SistemaConfig.java`, `PedidoProcesamientoFacade.java` |
| **Backend & Creacionales** | - Patrón Factory Method (`FacturaFactory`)<br>- Patrón Builder (`PedidoBuilder`)<br>- Persistencia JPA y DDL PostgreSQL | `FacturaFactory.java`, `PedidoBuilder.java`, `smartorders.sql` | `FacturaElectronicaFiscal.java`, `PedidoPersistenceAdapter.java` |
| **Estructurales & Integración Legacy** | - Patrón Adapter (`BuroCreditoAdapter`)<br>- Patrón Decorator (`AuditoriaTransaccionDecorator`)<br>- Desacoplamiento de servicios externos | ADR-003, Pruebas de integración | `BuroCreditoAdapter.java`, `AuditoriaTransaccionDecorator.java` |
| **Comportamiento & QA Tech Lead** | - Patrón Strategy (`DescuentoStrategy`)<br>- Patrón Observer (`PedidoEventPublisher`)<br>- Patrón State (`EstadoPedido`)<br>- Suite de pruebas JaCoCo (82%) | ADR-004, ADR-005, 32 Tests Automatizados | `DescuentoContext.java`, `PedidoObserverTest.java`, `target/site/jacoco/` |

---

### 3. Lecciones Aprendidas y Buenas Prácticas
1. **Separación de Responsabilidades:** Extraer el método monolítico `calcularDescuento()` hacia estrategias individuales demostró el poder del principio Open/Closed (OCP), permitiendo agregar nuevas promociones sin riesgo de romper las existentes.
2. **Inmutabilidad y Robustez:** El uso de Builder y la coacción defensiva de tipos numéricos (`double` con redondeo contable a 2 decimales) previene errores críticos de facturación.
3. **Observabilidad desde el Diseño:** Incorporar métricas y trazabilidad (Prometheus + Decorator de auditoría) reduce el tiempo medio de resolución de incidencias en producción (MTTR).
