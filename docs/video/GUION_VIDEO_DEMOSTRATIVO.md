# 🎬 GUION TÉCNICO PARA VIDEO DEMOSTRATIVO (5 - 7 MINUTOS)
## Proyecto: SmartOrders - Sistema Inteligente de Pedidos, Crédito y Facturación
### Asignatura: Patrones de Diseño de Software - Unidades Tecnológicas de Santander (UTS)
### Docente: Eliecer Montero Ojeda Ed.D

---

### ⏱️ CRONOGRAMA MINUTO A MINUTO

| Tiempo | Sección | Pantalla / Escena | Narración Técnica sugerida |
| :--- | :--- | :--- | :--- |
| **00:00 - 01:00** | **Introducción y Problema Legacy** | Diapositiva inicial con logo UTS y código original con `if-else` en `calcularDescuento()`. | *"Buenos días profesor Eliecer y compañeros. Presentamos el proyecto SmartOrders. Inicialmente contábamos con un modelo monolítico acoplado donde la clase Pedido violaba OCP y SRP, con condicionales rígidos de descuento y validaciones de crédito sin tipado defensivo. Hoy demostraremos la refactorización integral hacia Arquitectura Hexagonal y 9 Patrones GoF."* |
| **01:00 - 02:15** | **Arquitectura Hexagonal & ADRs** | Diagrama de Arquitectura Hexagonal (Mermaid/IDE) mostrando Dominio, Puertos y Adaptadores. | *"Adoptamos Arquitectura Hexagonal separando el dominio puro de los adaptadores web REST y persistencia JPA. Como se documenta en el ADR-001, esto nos permite alternar entre H2 en memoria y PostgreSQL sin tocar la lógica de negocio. Además, implementamos 9 patrones GoF: 3 creacionales, 3 estructurales y 3 de comportamiento."* |
| **02:15 - 03:30** | **Demostración: Patrones Creacionales y Estructurales** | IntelliJ / Terminal ejecutando pruebas y Postman/cURL. | *"En los creacionales: Singleton en SistemaConfig asegura un único estado del motor de órdenes; Factory Method en FacturaFactory emite facturas estándar o electrónicas con CUFE; y Builder en PedidoBuilder construye pedidos complejos inmutables. En los estructurales: Adapter en BuroCreditoAdapter homologa servicios externos de riesgo; Decorator en AuditoriaTransaccionDecorator agrega trazabilidad en tiempo real; y Facade unifica todo el flujo."* |
| **03:30 - 04:45** | **Demostración: Patrones de Comportamiento** | Código de Strategy (VIP, Subsidio, etc.), Observer (Eventos) y State. | *"Para los de comportamiento: el patrón Strategy resuelve directamente el código legado, encapsulando las reglas VIP (15%), Subsidio (20%), Frecuente (10%) y Senior (5%). El patrón Observer notifica automáticamente a Bodega, Notificaciones y Auditoría ante cambios de estado. Y el patrón State controla las transiciones válidas de ciclo de vida del pedido."* |
| **04:45 - 05:45** | **Pruebas Automatizadas y JaCoCo (>= 80%)** | Terminal ejecutando `mvn clean verify`, mostrando los 32 tests pasando y el reporte HTML de JaCoCo al 82%. | *"Ejecutamos 'mvn clean verify'. La suite corre 32 pruebas automatizadas sin fallos, alcanzando una cobertura real de líneas del 82% certificada por el plugin JaCoCo, superando el umbral del 80% exigido por la rúbrica. Esto garantiza refactorización segura y confianza en producción."* |
| **05:45 - 06:45** | **CI/CD, Monitoreo y Conclusiones** | GitHub Actions Pipeline y endpoint `/actuator/prometheus` en el navegador. | *"El proyecto cuenta con integración continua en GitHub Actions que valida cada commit, y observabilidad en tiempo real a través de Spring Boot Actuator y Prometheus en /actuator/prometheus. Concluimos que el uso metódico de patrones GoF convierte deuda técnica en software de alta ingeniería, escalable y mantenible. Muchas gracias."* |

---

### 💡 Consejos de Grabación:
1. Usar resolución 1080p a 60fps con OBS Studio o grabador del sistema.
2. Mantener la terminal con texto legible (fuente tamaño 14-16).
3. Tener abiertas las pestañas: Visual Studio Code / IntelliJ, navegador en `/actuator/prometheus` y el reporte JaCoCo `target/site/jacoco/index.html`.
