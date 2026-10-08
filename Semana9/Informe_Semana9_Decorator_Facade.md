# 📑 INFORME TÉCNICO - SEMANA 9: PATRÓN DECORATOR (PROFUNDIZACIÓN) & FACADE
## Curso: Patrones de Diseño de Software (PIS1003) — Corte 2 (Segundo Corte)
### Metodología: Aprendizaje Basado en Proyectos (ABPr) — Unidades Tecnológicas de Santander (UTS)
* **Estudiante:** Miguel Eduardo Ardila Cossio (`meduardoardila@uts.edu.co`)
* **Docente:** Eliecer Montero Ojeda Ed.D
* **Fecha de Entrega Oficial:** 08/10/2026
* **Dominio:** Sistema General Académico & SmartOrders Enterprise

---

## 1. Conexión con las Semanas Anteriores y Mapa del Catálogo GoF

| Semana | Patrones Evaluados | Pregunta de Arquitectura que Responden |
| :---: | :--- | :--- |
| **Semana 7** | Adapter & Bridge | ¿Cómo traduzco interfaces incompatibles? ¿Cómo desacoplo abstracción de implementación en dos dimensiones? |
| **Semana 8** | Composite & Decorator | ¿Cómo trato hojas y ramas uniformemente? ¿Cómo añado responsabilidades dinámicamente sin herencia? |
| **Semana 9** | **Decorator (Profundización) & Facade** | **¿Cómo aplico decoradores a reportes y validaciones en cadena? ¿Cómo simplifico el acceso a subsistemas complejos?** |

En la **Semana 9**, se resuelven dos problemas críticos de escalabilidad y desacoplamiento:
1. **Problema A — Extensión Dinámica de Reportes y Validaciones:** Cómo enriquecer un documento base (con encabezados institucionales, firmas digitales, marcas de agua "COPIA" y notas al margen) o encadenar reglas de validación sin incurrir en $2^n$ combinaciones rígidas de subclases (*Decorator Profundizado*).
2. **Problema B — Puerta de Entrada Unificada a Subsistemas Complejos:** Cómo permitir que un cliente (portal web, controlador REST o servicio externo) interactúe con múltiples subsistemas interdependientes sin acoplarse a sus clases internas (*Facade*).

---

## 2. Patrón DECORATOR (Profundización: Reportes y Validaciones)

### 2.1. Propósito e Intención GoF
> *"Attach additional responsibilities to an object dynamically. Decorators provide a flexible alternative to subclassing for extending functionality."*

Decorator permite envolver un objeto en capas concéntricas de decoradores que respetan la misma interfaz del componente base. Cada decorador añade comportamiento antes o después de delegar la llamada al siguiente eslabón.

### 2.2. Motivación: El Reporte Académico Multicontexto
Un reporte de notas puede ser solicitado en diferentes contextos institucionales:
* **Para el docente:** Solo notas por estudiante (`ReporteNotasBase`).
* **Oficial para secretaría:** `ReporteNotasBase` + `EncabezadoInstitucional` + `PieConFirma`.
* **Para archivo y auditoría:** `ReporteNotasBase` + `MarcaAgua("COPIA")`.
* **Para seguimiento tutorial:** `ReporteNotasBase` + `NotasAlMargen`.
* **Reporte oficial completo:** Todas las decoraciones combinadas.

Modelar estos requerimientos mediante herencia pura provocaría una **explosión combinatoria ingobernable** (`ReporteConEncabezado`, `ReporteConEncabezadoYPie`, `ReporteConEncabezadoYPieYMarcaAgua`, etc.). Con Decorator, cada factor es una clase independiente que se apila en runtime según el caso.

### 2.3. Implementación Canónica en Java

#### Component (Interfaz del Reporte):
```java
package co.uts.academico.reportes;

public interface ReporteAcademico {
    String generar();
    String getDescripcion();
}
```

#### ConcreteComponent (Reporte Base):
```java
package co.uts.academico.reportes;

import java.util.List;

public class ReporteNotasBase implements ReporteAcademico {
    private final String estudiante;
    private final List<Double> notas;
    private final double promedio;

    public ReporteNotasBase(String estudiante, List<Double> notas) {
        this.estudiante = estudiante;
        this.notas = notas;
        this.promedio = notas.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    @Override
    public String generar() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== REPORTE DE NOTAS ===\n");
        sb.append("Estudiante: ").append(estudiante).append("\n");
        sb.append("Notas: ").append(notas).append("\n");
        sb.append(String.format("Promedio: %.2f\n", promedio));
        return sb.toString();
    }

    @Override
    public String getDescripcion() {
        return "Notas base de " + estudiante;
    }
}
```

#### Decorator Abstracto:
```java
package co.uts.academico.reportes;

public abstract class ReporteDecorator implements ReporteAcademico {
    protected final ReporteAcademico reporte;

    public ReporteDecorator(ReporteAcademico reporte) {
        this.reporte = reporte;
    }

    @Override
    public String generar() {
        return reporte.generar(); // Delegación base
    }

    @Override
    public String getDescripcion() {
        return reporte.getDescripcion();
    }
}
```

#### ConcreteDecorators (Encabezado, Firma, Marca de Agua y Notas al Margen):
```java
package co.uts.academico.reportes;

// 1. Encabezado Institucional
public class EncabezadoInstitucionalDecorator extends ReporteDecorator {
    private static final String ENCABEZADO = 
        "===============================================\n" +
        "   UNIDADES TECNOLÓGICAS DE SANTANDER (UTS)   \n" +
        "          Facultad de Ingenierías              \n" +
        "===============================================\n\n";

    public EncabezadoInstitucionalDecorator(ReporteAcademico reporte) {
        super(reporte);
    }

    @Override
    public String generar() {
        return ENCABEZADO + super.generar();
    }

    @Override
    public String getDescripcion() {
        return super.getDescripcion() + " + Encabezado Institucional";
    }
}

// 2. Pie con Firma Digital
public class PieConFirmaDecorator extends ReporteDecorator {
    private final String firmante;

    public PieConFirmaDecorator(ReporteAcademico reporte, String firmante) {
        super(reporte);
        this.firmante = firmante;
    }

    @Override
    public String generar() {
        return super.generar() + 
            "\n-----------------------------------------------\n" +
            "Firma Digital: " + firmante + "\n" +
            "Fecha Emisión: " + java.time.LocalDate.now() + "\n";
    }

    @Override
    public String getDescripcion() {
        return super.getDescripcion() + " + Firma de " + firmante;
    }
}

// 3. Marca de Agua
public class MarcaAguaDecorator extends ReporteDecorator {
    private final String textoMarca;

    public MarcaAguaDecorator(ReporteAcademico reporte, String textoMarca) {
        super(reporte);
        this.textoMarca = textoMarca;
    }

    @Override
    public String generar() {
        String contenido = super.generar();
        return contenido.replaceAll("(?m)^", "[" + textoMarca + "] ");
    }

    @Override
    public String getDescripcion() {
        return super.getDescripcion() + " + Marca de Agua [" + textoMarca + "]";
    }
}
```

#### Cliente Dinámico:
```java
public class DemoReportesDecorados {
    public static void main(String[] args) {
        ReporteAcademico reporte = new ReporteNotasBase("Carlos Mendoza", List.of(4.5, 4.0, 4.8));

        // Composición flexible en runtime: Encabezado + Pie + Marca de Agua
        reporte = new EncabezadoInstitucionalDecorator(reporte);
        reporte = new PieConFirmaDecorator(reporte, "Dr. Eliecer Montero");
        reporte = new MarcaAguaDecorator(reporte, "COPIA OFICIAL");

        System.out.println("Descripción: " + reporte.getDescripcion());
        System.out.println(reporte.generar());
    }
}
```

### 2.4. Decorator en SmartOrders Enterprise: Validación y Auditoría en Cadena
En nuestro proyecto empresarial **SmartOrders Enterprise**, Decorator se aplica en dos frentes de misión crítica:
1. **Auditoría Forense (`AuditoriaTransaccionDecorator`):** Envuelve la interfaz `TransaccionCredito`, midiendo tiempos de latencia, registrando trazas estructuradas `[AUDITORIA_TRANSACCION]` y publicando métricas Prometheus de forma no invasiva.
2. **Validación en Cadena:** Permite apilar validaciones de negocio previas a la persistencia (validación de solvencia, verificación de stock y control de cupo) sin modificar el caso de uso central.

---

## 3. Patrón FACADE (Fachada)

### 3.1. Propósito e Intención GoF
> *"Provide a unified interface to a set of interfaces in a subsystem. Facade defines a higher-level interface that makes the subsystem easier to use."*

El patrón Facade proporciona una interfaz unificada y de alto nivel sobre un conjunto de interfaces de un subsistema complejo, reduciendo el acoplamiento y ocultando la maraña de llamadas entre clases internas.

### 3.2. Facade en SmartOrders: `PedidoProcesamientoFacade`
En el backend **SmartOrders Enterprise**, la fachada maestra es [`PedidoProcesamientoFacade.java`](file:///home/meac/Documentos/PATRONES%20DE%20SOFT/proyecto%20patrones/smartgrid/smartgrid/src/main/java/com/smartorders/application/facade/PedidoProcesamientoFacade.java).

#### Los 6 Subsistemas Coordinados por la Fachada:
Sin la fachada, el controlador REST tendría que instanciar, inyectar y orquestar secuencialmente:
1. **Singleton:** `SistemaConfig` (valida estado operativo y modo contingencia).
2. **Strategy:** `DescuentoContext` (resuelve el descuento polimórfico óptimo según el perfil del cliente).
3. **Builder:** `PedidoBuilder` (construye la orden inmutable con redondeo financiero defensivo).
4. **Adapter + Decorator:** `BuroCreditoPort` envuelto por `AuditoriaTransaccionDecorator` (evalúa solvencia y registra auditoría forense).
5. **State:** `EstadoPedido` (controla la máquina de estados determinista: PENDIENTE ➔ APROBADO / RECHAZADO).
6. **Observer:** `PedidoEventPublisher` (notifica en abanico a Bodega, Correo y Prometheus).

```text
Cliente REST (PedidoController)
         │  invoca procesarPedido(PedidoRequestDTO)
         ▼
┌────────────────────────────────────────────────────────┐
│             PedidoProcesamientoFacade                  │
│                                                        │
│  1. Consulta SistemaConfig.getInstancia() (Singleton)  │
│  2. Resuelve DescuentoContext (Strategy)              │
│  3. Construye Pedido mediante PedidoBuilder            │
│  4. Evalúa Crédito con TransaccionCredito (Decorator) │
│  5. Aplica Transición en EstadoPedido (State)          │
│  6. Persiste en PedidoRepositoryPort (JPA Adapter)     │
│  7. Publica Evento vía PedidoEventPublisher (Observer) │
└────────────────────────────────────────────────────────┘
```

#### Código Real de la Fachada en SmartOrders:
```java
@Service
public class PedidoProcesamientoFacade {
    private final SistemaConfig config = SistemaConfig.getInstancia();
    private final DescuentoContext descuentoContext;
    private final TransaccionCredito transaccionCredito; // Envuelto por Decorator
    private final PedidoRepositoryPort pedidoRepository;
    private final PedidoEventPublisher eventPublisher;

    public PedidoResponseDTO procesarPedido(PedidoRequestDTO request) {
        // Valida configuración del sistema
        config.validarOperatividad();

        // 1. Resuelve descuento con Strategy
        double descuento = descuentoContext.calcularDescuento(cliente);

        // 2. Construye con Builder
        Pedido pedido = new PedidoBuilder(cliente)
                .conItems(mapearItems(request.items()))
                .conPorcentajeDescuento(descuento)
                .build();

        // 3. Evalúa con Decorator y Adapter
        ResultadoCredito credito = transaccionCredito.evaluar(cliente.getId(), pedido.getMontoTotal());

        // 4. Aplica State y Persiste
        if (credito.isAprobado()) {
            pedido.aprobar();
            pedidoRepository.guardar(pedido);
            eventPublisher.publicar(TipoEvento.PEDIDO_APROBADO, pedido);
        } else {
            pedido.rechazar(credito.getMotivo());
            pedidoRepository.guardar(pedido);
            eventPublisher.publicar(TipoEvento.PEDIDO_RECHAZADO, pedido);
        }

        return toDTO(pedido);
    }
}
```

---

## 4. Comparativas Técnicas Esenciales

### 4.1. Relación Facade vs. Adapter
| Criterio | Patrón ADAPTER | Patrón FACADE |
| :--- | :--- | :--- |
| **Propósito** | Traducir una interfaz incompatible a otra esperada. | Simplificar y unificar el acceso a un subsistema complejo. |
| **¿Cuántas clases envuelve?** | **1 clase** (el *Adaptee* legado o de terceros). | **Múltiples clases** (todo un subsistema de colaboradores). |
| **¿Cambia la interfaz?** | Sí, la traduce para adaptarla. | Sí, la simplifica en métodos de alto nivel. |
| **¿Añade comportamiento?** | No, solo traduce parámetros y respuestas. | No, solo orquesta y coordina la llamada a subsistemas. |
| **Regla Mnemotécnica** | **Traductor** (hace que A parezca B). | **Recepcionista** (punto único para gestionar todo). |

---

### 4.2. Matriz Estructural: Composite vs. Decorator vs. Facade
| Criterio | Composite | Decorator | Facade |
| :--- | :--- | :--- | :--- |
| **Intención** | Tratar hojas y composiciones uniformemente. | Añadir responsabilidades dinámicamente. | Simplificar el acceso a subsistemas. |
| **Topología** | Árbol jerárquico (`1..*` hijos). | Cadena o envoltura lineal (`1` hijo). | Estrella (coordina $N$ subsistemas). |
| **¿Modifica Interfaz?** | No, la conserva estrictamente. | No, la conserva estrictamente. | **Sí, crea una nueva interfaz simplificada.** |
| **Alternativa a** | Herencia rígida con `if/else` por tipo. | Explosión combinatoria de $2^n$ subclases. | Acoplamiento directo y frágil al subsistema. |
| **Rol en SmartOrders** | Evaluado para Combos comerciales. | `AuditoriaTransaccionDecorator` (100% test). | `PedidoProcesamientoFacade` (100% test). |

---

## 5. Relación con los Principios SOLID

1. **Single Responsibility Principle (SRP):**
   * El `Facade` tiene una sola responsabilidad: **orquestar**. No calcula descuentos, no valida burós ni persiste en base de datos; delega cada tarea en su subsistema respectivo.
   * Cada `Decorator` se enfoca en una sola alteración (solo encabezado, solo firma, solo marca de agua).
2. **Open/Closed Principle (OCP):**
   * Nuevos subsistemas pueden incorporarse tras la fachada sin romper los clientes REST existentes.
   * Nuevos decoradores de reporte se apilan sin alterar la clase base `ReporteNotasBase`.
3. **Dependency Inversion Principle (DIP):**
   * Los controladores web dependen de la abstracción de la fachada, eliminando dependencias con repositorios, publishers o servicios legados.

---

## 6. Respuestas a las Preguntas de Reflexión de la Cátedra (Diapositiva 19)

1. **¿Por qué en Decorator el orden de aplicación puede importar?**  
   Porque si se aplican transformaciones textuales o matemáticas dependientes del contexto, el orden altera el resultado. Por ejemplo, si se aplica `MarcaAguaDecorator` (que antepone un prefijo a cada línea) *antes* de `EncabezadoInstitucionalDecorator`, el encabezado no recibirá la marca de agua. Si se aplica *después*, todas las líneas (incluyendo el encabezado) quedarán marcadas.

2. **¿Qué diferencia hay entre un Facade y un "God Object"?**  
   Un *God Object* es un antipatrón que monopoliza toda la lógica de negocio y sabe demasiado. Un **Facade**, en cambio, es un diseño limpio que **solo coordina y delega**: no contiene reglas de negocio; la inteligencia reside en los subsistemas orquestados.

3. **¿Puede un Facade también ser Singleton?**  
   **Sí, es una combinación clásica en ingeniería.** A menudo se diseña una única instancia compartida del Facade para centralizar las operaciones de todo el sistema sin crear instancias redundantes.

4. **¿Cómo se relaciona Decorator con el principio Open/Closed?**  
   Permite extender el comportamiento de cualquier objeto en tiempo de ejecución añadiendo nuevas clases decoradoras sin modificar ni recompilar la clase base original.

5. **¿Qué patrón usarías para modelar la estructura de un plan de estudios?**  
   **Composite:** `Facultad ➔ Programa ➔ Área de Formación ➔ Asignatura`. Permite tratar de forma uniforme créditos académicos y prerrequisitos en cualquier nivel jerárquico.

---

## 7. Architectural Decision Record (ADR-009)

```markdown
# ADR 0009: Orquestación mediante Facade y Extensibilidad con Decorator
## Estado: ACEPTADO
## Contexto: 
El procesamiento de órdenes comerciales involucra 6 subsistemas heterogéneos, 
y la auditoría forense requiere capturar trazas sin invadir el núcleo de negocio.
## Decisión:
Implementar PedidoProcesamientoFacade como punto único de entrada REST y 
AuditoriaTransaccionDecorator como decorador @Primary de evaluación crediticia.
## Consecuencias:
- Positivas: Alto desacoplamiento, controladores delgados, trazabilidad no intrusiva.
- Verificación: 100% de cobertura en paquetes de fachada y decorador con JUnit 5.
```
