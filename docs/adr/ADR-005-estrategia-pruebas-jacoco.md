# ADR-005: Estrategia de Pruebas Automatizadas y Umbral de Cobertura JaCoCo (>= 80%)

## Estado
**Aceptado**

## Contexto
La rúbrica de evaluación de la asignatura de Patrones de Diseño (UTS) exige explícitamente:
- Pruebas automatizadas con cobertura $\ge 80\%$.
- Confianza en el código, mantenibilidad y refactorización segura.

## Decisión
1. **Frameworks de Testing:**
   - **JUnit 5 (Jupiter):** Pruebas unitarias de dominio, patrones y casos de uso.
   - **Mockito:** Mocking de puertos y dependencias secundarias en Facade y Decorator.
   - **Spring Boot MockMvc:** Pruebas de integración sobre controladores REST HTTP.
2. **JaCoCo Plugin (`jacoco-maven-plugin:0.8.12`):**
   - Configurado en la fase `test` y `verify` de Maven.
   - Regla de fallo estricta: `<minimum>0.80</minimum>` sobre líneas de código cubiertas (`LINE COVEREDRATIO`).
   - El build falla automáticamente en CI/CD si la cobertura desciende del 80%.

## Resultados
- **Tests ejecutados:** 32 pruebas automatizadas.
- **Fallos / Errores:** 0.
- **Cobertura alcanzada:** **82%** de líneas cubiertas en el bundle completo.
