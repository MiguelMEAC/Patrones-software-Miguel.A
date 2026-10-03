# 📊 PRESENTACIÓN EJECUTIVA: SMARTORDERS ENTERPRISE
## Sistema Inteligente de Gestión de Pedidos, Crédito y Facturación Dinámica
### Unidades Tecnológicas de Santander (UTS) - Ingeniería de Software
**Docente:** Eliecer Montero Ojeda Ed.D

---

## 🗂️ Diapositiva 1: Portada
- **Título:** SmartOrders Enterprise
- **Subtítulo:** Refactorización Arquitectónica Hexagonal y Aplicación de 9 Patrones GoF
- **Autor / Estudiante:** Miguel Eduardo Ardila Cossio (`meduardoardila@uts.edu.co`)
- **Materia:** Patrones de Diseño de Software

---

## 🗂️ Diapositiva 2: El Desafío (Código Legacy y Deuda Técnica)
- Monolito inicial con alta duplicación y acoplamiento.
- Violación de Principios SOLID:
  - **OCP:** `calcularDescuento()` con cadenas condicionales rígidas.
  - **SRP:** Clase `Pedido` asumiendo validaciones financieras, fechas obsoletas (`Calendar`) y estado.
  - **Demeter:** Envidia de atributos sobre el perfil y datos fiscales del cliente.
- Falta de pruebas automatizadas y observabilidad.

---

## 🗂️ Diapositiva 3: Arquitectura Hexagonal (Ports & Adapters)
- **Dominio Central:** Puro, desacoplado, sin dependencias de infraestructura ni frameworks.
- **Puertos de Entrada:** `CrearPedidoUseCase`, `ConsultarPedidoUseCase`, `FacturarPedidoUseCase`.
- **Puertos de Salida:** Repositorios, scoring de riesgo y notificaciones.
- **Adaptadores:** REST Controllers, JPA PostgreSQL / H2 en memoria, Buro Legacy Adapter.

---

## 🗂️ Diapositiva 4: Catálogo de Patrones GoF Implementados (9 Patrones)
| Categoría | Patrón GoF | Componente en el Proyecto |
| :--- | :--- | :--- |
| **Creacionales** | **Singleton** | `SistemaConfig`: Configuración y control global del motor. |
| **Creacionales** | **Factory Method** | `FacturaFactory`: Emisión estándar y electrónica fiscal (CUFE). |
| **Creacionales** | **Builder** | `PedidoBuilder`: Construcción inmutable y validación de invariantes. |
| **Estructurales** | **Adapter** | `BuroCreditoAdapter`: Homologación de central de riesgo legacy. |
| **Estructurales** | **Decorator** | `AuditoriaTransaccionDecorator`: Trazabilidad forense transparente. |
| **Estructurales** | **Facade** | `PedidoProcesamientoFacade`: Orquestación simple de subsistemas. |
| **Comportamiento** | **Strategy** | `DescuentoStrategy`: VIP (15%), Subsidio (20%), Frecuente (10%), Senior (5%). |
| **Comportamiento** | **Observer** | `PedidoEventPublisher`: Notificaciones a Bodega, Correo y Métricas. |
| **Comportamiento** | **State** | `EstadoPedido`: Control determinista del ciclo de vida (`PENDIENTE` a `COMPLETADO`). |

---

## 🗂️ Diapositiva 5: Estrategia de Testing y Calidad (JaCoCo >= 80%)
- **32 Pruebas Automatizadas** (Unitarias, de Integración y MockMvc).
- **Cobertura Real Alcanzada:** **82%** verificada por `jacoco-maven-plugin`.
- Regla de fallo automática en build si la cobertura desciende del 80%.

---

## 🗂️ Diapositiva 6: Integración Continua (CI/CD) & Observabilidad
- **GitHub Actions:** Pipeline automatizado (`.github/workflows/ci.yml`) en cada push/PR.
- **Observabilidad:**
  - Spring Boot Actuator (`/actuator/health`, `/actuator/metrics`).
  - Prometheus Metric Exposer (`/actuator/prometheus`).
  - Logs estructurados con correlación de hilos y timestamps.

---

## 🗂️ Diapositiva 7: Conclusiones y Retorno de Inversión (ROI)
- Eliminación total de acoplamiento en reglas de descuento.
- Flexibilidad para cambiar de base de datos sin afectar la lógica de negocio.
- Software preparado para producción, resiliente, documentado bajo estándares de ingeniería y evaluable mediante ADRs.
