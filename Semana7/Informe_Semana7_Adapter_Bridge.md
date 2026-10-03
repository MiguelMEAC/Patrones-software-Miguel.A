# 📑 INFORME TÉCNICO - SEMANA 7: PATRONES ESTRUCTURALES ADAPTER & BRIDGE
## Curso: Patrones de Diseño de Software (PIS1003) — Unidades Tecnológicas de Santander (UTS)
### Proyecto Integrador: SmartOrders Enterprise
* **Estudiante:** Miguel Eduardo Ardila Cossio (`meduardoardila@uts.edu.co`)
* **Docente:** Eliecer Montero Ojeda Ed.D
* **Fecha de Entrega Oficial:** 03/10/2026
* **Metodología:** Aprendizaje Basado en Proyectos (ABPr)

---

## 1. Introducción y Ubicación de los Patrones Estructurales

Los patrones estructurales se ocupan de cómo se componen las clases y los objetos para formar estructuras más grandes, manteniendo la flexibilidad y la eficiencia. Mientras los patrones creacionales (vistos en el Corte 1: Singleton, Factory Method, Builder) resuelven *cómo crear objetos*, los patrones estructurales resuelven *cómo organizar y conectar clases y objetos ya creados para que colaboren entre sí*.

En la **Semana 7**, el estudio se concentra en dos patrones que frecuentemente se confunden pero que responden a intenciones de diseño completamente distintas:
* **Adapter:** Resuelve un problema de compatibilidad a posteriori (*algo ya existe y no encaja con nuestro sistema*).
* **Bridge:** Resuelve un problema de diseño a priori (*un concepto va a variar en dos dimensiones ortogonales y queremos evitar una explosión combinatoria desde el inicio*).

---

## 2. Patrón ADAPTER (Adaptador)

### 2.1. Propósito e Intención GoF
> *"Convert the interface of a class into another interface clients expect. Adapter lets classes work together that couldn't otherwise because of incompatible interfaces."*

El patrón Adapter actúa como un intermediario o "traductor" que envuelve un objeto existente (`Adaptee`) y expone una interfaz estándar que el cliente espera (`Target`), traduciendo los parámetros, tipos de datos y respuestas entre ambas partes.

### 2.2. Implementación en SmartOrders Enterprise: ¿Por qué SÍ se pudo implementar?

En el proyecto **SmartOrders**, el patrón Adapter **fue implementado de forma exitosa y obligatoria** para cumplir con la Arquitectura Hexagonal (Puertos y Adaptadores).

#### Problema en el Dominio:
El sistema requiere evaluar la solvencia crediticia del cliente antes de aprobar un pedido. Para ello, se conecta con una entidad externa de centrales de riesgo bancarias representada por la clase `BuroCreditoLegacyService`. 
Dicho servicio legacy presenta una interfaz incompatible y propietaria:
```java
// Interfaz Legacy heredada de terceros (Adaptee):
public class BuroCreditoLegacyService {
    public LegacyCreditReport consultarHistorial(String idCliente) {
        // Retorna un objeto LegacyCreditReport incompatible con el dominio
    }
}
```

#### Solución Mediante el Patrón:
1. **Target (Puerto de Salida del Dominio):** Se definió el contrato agnóstico `BuroCreditoPort`:
   ```java
   public interface BuroCreditoPort {
       ResultadoCredito evaluar(String clienteId, double monto);
   }
   ```
2. **Adapter (Adaptador Concreto de Infraestructura):** Se implementó `BuroCreditoAdapter`, el cual contiene por composición al servicio legacy y traduce la llamada:
   ```java
   @Component
   public class BuroCreditoAdapter implements BuroCreditoPort {
       private final BuroCreditoLegacyService legacyService;

       public BuroCreditoAdapter(BuroCreditoLegacyService legacyService) {
           this.legacyService = legacyService;
       }

       @Override
       public ResultadoCredito evaluar(String clienteId, double monto) {
           LegacyCreditReport report = legacyService.consultarHistorial(clienteId);
           boolean aprobado = report.getScore() >= 600 && !report.isEnMora();
           String motivo = aprobado ? "Solvente" : "Score insuficiente: " + report.getScore();
           return new ResultadoCredito(aprobado, report.getScore(), motivo);
       }
   }
   ```

#### Justificación de Ingeniería:
* **Principio de Inversión de Dependencias (DIP):** El caso de uso solo conoce `BuroCreditoPort`; desconoce por completo la existencia de `BuroCreditoLegacyService`.
* **Mantenibilidad:** Si mañana la entidad financiera cambia su API por una REST JSON o gRPC, solo se modifica o sustituye el adaptador, manteniendo el núcleo del negocio 100% inalterado.

---

## 3. Patrón BRIDGE (Puente)

### 3.1. Propósito e Intención GoF
> *"Decouple an abstraction from its implementation so that the two can vary independently."*

Bridge separa una jerarquía de clases en dos jerarquías paralelas e independientes conectadas por composición en lugar de herencia:
1. **Abstracción:** Jerarquía de alto nivel (qué se hace).
2. **Implementador:** Jerarquía de bajo nivel o plataforma técnica (cómo y por dónde se hace).

Su principal motivación es erradicar la **explosión combinatoria de subclases** ($M \text{ tipos} \times N \text{ canales} = M \times N \text{ clases}$).

### 3.2. Análisis en SmartOrders Enterprise: ¿Por qué NO se implementó o se descartó?

En el proyecto **SmartOrders**, el patrón Bridge fue formalmente evaluado en el diseño y **descartado por justificación de ingeniería** para evitar el antipatrón de **Sobrediseño (*Over-engineering*)**.

#### Razones del Descarte Técnico:
1. **Ausencia de dos dimensiones ortogonales concurrentes:** 
   En el núcleo comercial de SmartOrders (Pedidos, Clientes y Facturas), las variaciones son de comportamiento algorítmico (descuentos) o de estados deterministas (ciclo de vida). No existen dos dimensiones estructurales independientes que justifiquen desacoplar una abstracción de un implementador mediante dos jerarquías en paralelo.
2. **Resolución Limpia Mediante Patrón Observer:**
   Para las alertas y notificaciones a clientes, el proyecto integró el patrón **Observer** (`PedidoEventPublisher` notificando a `NotificacionClienteObserver`, `InventarioObserver` y `AuditoriaObserver`). El Observer resuelve el desacoplamiento de eventos de forma mucho más idiomática en Spring Boot que un Bridge.
3. **Cumplimiento de la Advertencia Oficial del Docente (Diapositiva 8):**
   Tal como lo establece la presentación oficial de la cátedra:
   > *"Si solo hay una dimensión que varía, Bridge es over-engineering; conviene usarlo cuando existen dos o más ejes de variación reales. No lo uses si solo una dimensión varía: ahí bastan herencia o Strategy."*
   Forzar un patrón Bridge donde solo varía el canal de notificación o el tipo de factura crearía clases abstractas redundantes violando el principio KISS (*Keep It Simple, Stupid*).

---

### 3.3. Propuesta Arquitectónica: ¿Cómo SÍ aplicaría Bridge en SmartOrders?

Si el negocio evolucionara y requiriera una generación avanzada de **Reportes y Certificados Comerciales Multiformato**, el patrón Bridge sería la solución exacta para evitar la explosión combinatoria:

```text
       Dimensión 1 (Abstracción)                      Dimensión 2 (Implementador)
             ReporteComercial                              MotorRenderizado
                    │                                             │
      ┌─────────────┴─────────────┐                 ┌─────────────┴─────────────┐
ReporteFactura  ReporteCertificadoPazYSalvo    RenderizadorPDF  RenderizadorExcel  RenderizadorJSON
```

* **Estructura:**
  * **Abstracción:** `abstract class ReporteComercial(MotorRenderizado motor)`
    * `ReporteFactura extends ReporteComercial`
    * `ReporteCertificadoPazYSalvo extends ReporteComercial`
  * **Implementador:** `interface MotorRenderizado`
    * `RenderizadorPDF implements MotorRenderizado`
    * `RenderizadorExcel implements MotorRenderizado`
    * `RenderizadorJSON implements MotorRenderizado`
* **Beneficio:** 2 tipos de reportes $\times$ 3 motores de renderizado = 6 clases ($2 + 3$). Si se usara herencia pura, se requerirían $2 \times 3 = 6$ clases combinadas (`ReporteFacturaPDF`, `ReporteFacturaExcel`, `ReporteFacturaJSON`, etc.), y agregar un formato como HTML implicaría crear múltiples clases hijas en lugar de solo una.

---

## 4. Comparativa Técnica Exhaustiva: ADAPTER vs. BRIDGE

| Criterio de Comparación | Patrón ADAPTER | Patrón BRIDGE |
| :--- | :--- | :--- |
| **Intención Principal** | Hacer compatibles interfaces incompatibles (*arregla lo existente*). | Desacoplar abstracción de implementación (*diseño desde el inicio*). |
| **Momento en que se aplica** | **Después:** Cuando ya existe código legacy o librerías de terceros. | **Antes / Durante:** En el diseño arquitectónico inicial del sistema. |
| **Motivación Típica** | Integrar APIs externas heterogéneas (ej. Stripe, Buró de Crédito). | Evitar la explosión combinatoria de subclases ($M \times N$). |
| **Relación entre Participantes** | El Adapter envuelve al Adaptee (`Adapter has-a Adaptee`). | La Abstracción contiene una referencia al Implementador (`Bridge`). |
| **Interfaz del Envoltorio** | Incompatible previamente con el Target. | Diseñada y acordada de mutuo acuerdo entre diseñadores. |
| **Número de Jerarquías** | Una jerarquía (el Adapter implementa el Target del cliente). | Dos jerarquías paralelas independientes conectadas por un puente. |
| **Cambio en Runtime** | Generalmente fijo por configuración del proveedor. | Altamente dinámico: se puede intercambiar el implementador en caliente. |
| **Riesgo / Antipatrón** | Confundirlo con Facade o crear un *God Object*. | **Over-engineering** si solo existe una dimensión real de variación. |
| **Rol en SmartOrders** | **✅ Implementado** en `BuroCreditoAdapter`. | **🚫 Descartado técnicamente** para evitar sobre-ingeniería innecesaria. |

> 💡 **Regla Mnemotécnica de la Cátedra:**  
> * **Adapter** = *Traductor* (hace encajar un enchufe que no entraba).  
> * **Bridge** = *Arquitecto* (diseña dos caminos paralelos desde los cimientos).

---

## 5. Relación con los Principios SOLID

1. **Open/Closed Principle (OCP):**
   * *Adapter:* Permite integrar nuevos proveedores de crédito (Datacrédito, TransUnion) creando nuevos adaptadores sin modificar el código del caso de uso.
   * *Bridge:* Permite agregar nuevos canales o nuevos tipos de reportes sin alterar las clases existentes.
2. **Dependency Inversion Principle (DIP):**
   * El cliente de alto nivel (`PedidoProcesamientoFacade`) depende únicamente de interfaces abstractas (`BuroCreditoPort`), nunca de servicios externos concretos (`BuroCreditoLegacyService`).
3. **Single Responsibility Principle (SRP):**
   * `BuroCreditoAdapter` tiene una única responsabilidad: homologar la respuesta legacy al modelo del dominio. No valida pedidos, no calcula impuestos ni guarda en base de datos.

---

## 6. Respuestas a las Preguntas de Reflexión de la Sesión

1. **¿Por qué en SmartOrders conviene un `BuroCreditoAdapter` en lugar de llamar directamente a `BuroCreditoLegacyService`?**  
   Porque si se llamara directamente desde el caso de uso, el modelo del dominio quedaría contaminado con clases legacy externas (`LegacyCreditReport`). Si el proveedor cambia su API o cambiamos de entidad bancaria, tendríamos que reescribir la lógica del negocio. El Adapter aísla y blinda el dominio.

2. **¿Qué pasaría si en las notificaciones usáramos herencia en lugar de Bridge?**  
   Ocurriría una explosión combinatoria. Con 3 tipos de notificación y 3 canales se tendrían 9 clases. Al agregar WhatsApp y un nuevo tipo ("Encuesta"), pasaríamos a 16 clases, haciendo el mantenimiento insostenible.

3. **¿Podrían combinarse Adapter y Bridge en el mismo módulo?**  
   **Sí, perfectamente.** Por ejemplo, un `CanalEmail` (que es un `ConcreteImplementor` dentro de un Bridge) puede internamente utilizar un `SendGridAdapter` para adaptar la librería propietaria del proveedor SendGrid a la interfaz interna del canal.

---

## 7. Conclusiones y Estado del Proyecto

1. **Adapter implementado y verificado:** `BuroCreditoAdapter` cuenta con pruebas unitarias automatizadas (`BuroCreditoAdapterTest`) superando el 97% de cobertura en su paquete.
2. **Bridge analizado y descartado por rigor arquitectónico:** Se documenta formalmente en el registro de decisiones de arquitectura (ADR) para justificar por qué no se introdujo complejidad innecesaria en el sistema.
3. **Integridad y Cobertura:** Todo el backend mantiene **32 pruebas unitarias en verde** y un **82.84% de cobertura de código en JaCoCo**.
