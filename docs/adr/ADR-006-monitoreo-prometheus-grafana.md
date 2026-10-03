# ADR-006: Monitoreo, Métricas Centralizadas y Observabilidad (Prometheus + Grafana)

## Estado
**Aceptado**

## Contexto
El lineamiento institucional exige observabilidad y logging centralizado (Prometheus + Grafana o ELK).

## Decisión
1. **Spring Boot Actuator:**
   - Exposición de endpoints de salud y métricas operativas (`/actuator/health`, `/actuator/metrics`, `/actuator/prometheus`).
2. **Micrometer Prometheus Registry:**
   - Dependencia `io.micrometer:micrometer-registry-prometheus` que formatea las métricas en formato scrapeable estándar para Prometheus Server.
3. **Métricas de Negocio Personalizadas:**
   - Mediante `AuditoriaObserver`, se emiten trazas y contadores de eventos de pedidos (`metric=pedidos_eventos_total`).
4. **Logging Estructurado:**
   - Formato de log con timestamp milimétrico, hilo de ejecución, nivel de severidad y clase emisora (`%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n`), facilitando la ingesta en Logstash/Elasticsearch o Datadog.
