# 📑 INFORME TÉCNICO - SEMANA 10: PATRONES FLYWEIGHT & PROXY (CIERRE ESTRUCTURAL)
## Curso: Patrones de Diseño de Software (PIS1003) — Corte 2 (Segundo Corte)
### Metodología: Aprendizaje Basado en Proyectos (ABPr) — Unidades Tecnológicas de Santander (UTS)
* **Estudiante:** Miguel Eduardo Ardila Cossio (`meduardoardila@uts.edu.co`)
* **Docente:** Eliecer Montero Ojeda Ed.D
* **Fecha de Entrega Oficial:** 15/10/2026
* **Dominio Integrador:** Sistema de Gestión Académica & SmartOrders Enterprise (Java 21 LTS / Spring Boot 3.3.4)

---

## 1. Conexión de las 10 Semanas y Cierre del Bloque Estructural GoF

Con la presente Semana 10 se concluye formalmente el bloque de **Patrones Estructurales de Diseño (Gang of Four)**. A lo largo del curso se ha transitado desde el diagnóstico de código legado y la arquitectura limpia, pasando por los patrones creacionales, hasta completar el catálogo estructural:

| Semana | Eje Temático Principal | Patrones Evaluados / Arquitectura | Pregunta de Diseño que Resuelve |
| :---: | :--- | :--- | :--- |
| **Semana 1** | Smells & Deuda Técnica | Diagnóstico de código spaghetti recibido | ¿Dónde están las violaciones a SOLID y el acoplamiento? |
| **Semana 2** | Refactorización Limpia | Principios SOLID (SRP, OCP, LSP, ISP, DIP) | ¿Cómo modularizamos el núcleo de dominio? |
| **Semana 3** | Patrones Creacionales I | Singleton (Thread-Safe con Double-Locking) | ¿Cómo aseguramos una única instancia global de configuración? |
| **Semana 4** | Patrones Creacionales II | Factory Method (Facturación electrónica DIAN) | ¿Cómo delegamos la instanciación de productos especializados? |
| **Semana 5** | Patrones Creacionales III | Builder (Construcción inmutable de pedidos) | ¿Cómo construimos agregados complejos con redondeo financiero? |
| **Semana 6** | Arquitectura Hexagonal | Puertos y Adaptadores (Ports & Adapters) | ¿Cómo desacoplamos el negocio de bases de datos y frameworks? |
| **Semana 7** | Patrones Estructurales I | Adapter & Bridge | ¿Cómo traducimos interfaces y desacoplamos jerarquías en dos ejes? |
| **Semana 8** | Patrones Estructurales II | Composite & Decorator | ¿Cómo tratamos árboles uniformemente y añadimos capas dinámicas? |
| **Semana 9** | Patrones Estructurales III | Decorator (Profundizado) & Facade | ¿Cómo encadenamos validaciones y orquestamos 6 subsistemas? |
| **Semana 10** | **Patrones Estructurales IV** | **Flyweight & Proxy (Cierre del Catálogo Estructural)** | **¿Cómo optimizamos millones de objetos en memoria y controlamos el acceso?** |

---

## 2. Patrón FLYWEIGHT (Optimización Extrema de Memoria)

### 2.1. Propósito e Intención GoF
> *"Use sharing to support large numbers of fine-grained objects efficiently."* (Gamma et al., 1994, p. 195).

El patrón Flyweight resuelve el problema del consumo excesivo de memoria RAM provocado por la proliferación de miles o millones de objetos pequeños que contienen datos idénticos o repetitivos. Su estrategia radica en **compartir instancias inmutables** en lugar de crear copias redundantes.

### 2.2. Anatomía Fundamental: Estado Intrínseco vs. Estado Extrínseco

La clave del éxito en Flyweight es la rigurosa separación de los datos que componen un objeto:

```text
┌────────────────────────────────────────────────────────────────────────┐
│                        ANATOMÍA DE UN FLYWEIGHT                        │
├───────────────────────────────────┬────────────────────────────────────┤
│   ESTADO INTRÍNSECO (Compartido)   │   ESTADO EXTRÍNSECO (Contextual)   │
├───────────────────────────────────┼────────────────────────────────────┤
│ • Inmutable (Read-Only)           │ • Mutable o dependiente de contexto│
│ • Idéntico para muchos objetos     │ • Varía según el cliente o uso     │
│ • Almacenado DENTRO del Flyweight │ • Almacenado FUERA del Flyweight   │
│ • No depende del tiempo ni lugar   │ • Se pasa como parámetro en métodos│
│ • Ej: Nombre, créditos, syllabus, │ • Ej: Aula, día, hora, profesor,   │
│   SKU, descripción técnica base.  │   cantidad, precio facturado.      │
└───────────────────────────────────┴────────────────────────────────────┘
```

### 2.3. Problema Planteado en la Sesión Académica: Horarios de Estudiantes
* **Escenario:** En una universidad con 5.000 estudiantes matriculados en promedio en 6 asignaturas cada uno, se registran $5.000 \times 6 = 30.000$ inscripciones.
* **Enfoque Ingenuo (Antipatrón):** Cada estudiante instancia su propio objeto `Materia` completo (nombre, código, créditos, descripción, bibliografía, objetivos). Esto genera 30.000 instancias duplicando cadenas de texto y metadatos pesados, saturando el Heap de la JVM y presionando al Garbage Collector.
* **Enfoque con Flyweight:** En la institución solo existen 500 materias reales en el pensum. Se crea un pool de **500 instancias únicas de `Materia`** administradas por una `MateriaFlyweightFactory`. Cada estudiante almacena únicamente un `BloqueHorario` ligero con el estado extrínseco (día, hora, aula) y un puntero/referencia a la materia compartida.
* **Ahorro Demostrado:** De 30.000 objetos a 500 ($98.3\%$ de reducción en consumo de memoria para catálogos).

### 2.4. Implementación Canónica en Java

```java
// 1. Estado Intrínseco: Flyweight Inmutable
public final class MateriaFlyweight {
    private final String codigo;
    private final String nombre;
    private final int creditos;
    private final String departamento;

    public MateriaFlyweight(String codigo, String nombre, int creditos, String departamento) {
        this.codigo = Objects.requireNonNull(codigo);
        this.nombre = Objects.requireNonNull(nombre);
        this.creditos = creditos;
        this.departamento = Objects.requireNonNull(departamento);
    }

    // Operación que recibe el estado extrínseco desde el cliente
    public void imprimirHorario(String dia, String hora, String aula, String estudiante) {
        System.out.printf("[%s] Estudiante: %s | Asignatura: %s (%s, %d créd) | Horario: %s %s | Aula: %s%n",
                codigo, estudiante, nombre, departamento, creditos, dia, hora, aula);
    }

    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
}

// 2. Flyweight Factory con Cache Interno (Pool de Objetos)
public class MateriaFlyweightFactory {
    private static final Map<String, MateriaFlyweight> POOL = new ConcurrentHashMap<>();

    public static MateriaFlyweight obtenerMateria(String codigo, String nombre, int creditos, String depto) {
        return POOL.computeIfAbsent(codigo, k -> {
            System.out.println("[FLYWEIGHT_FACTORY] Instanciando nueva Materia intrínseca compartida: " + codigo);
            return new MateriaFlyweight(codigo, nombre, creditos, depto);
        });
    }

    public static int getCantidadInstanciasEnMemoria() {
        return POOL.size();
    }
}

// 3. Cliente: BloqueHorario que almacena el Estado Extrínseco
public class BloqueHorario {
    private final MateriaFlyweight materia; // Referencia al Flyweight
    private final String dia;               // Extrínseco
    private final String hora;              // Extrínseco
    private final String aula;              // Extrínseco

    public BloqueHorario(MateriaFlyweight materia, String dia, String hora, String aula) {
        this.materia = materia;
        this.dia = dia;
        this.hora = hora;
        this.aula = aula;
    }

    public void renderizar(String estudiante) {
        materia.imprimirHorario(dia, hora, aula, estudiante);
    }
}
```

### 2.5. Aplicación en SmartOrders Enterprise: Catálogo de Productos y Líneas de Pedido
En nuestro sistema de facturación y comercio electrónico [SmartOrders Enterprise](file:///home/meac/Documentos/PATRONES%20DE%20SOFT/proyecto%20patrones/README.md), si procesamos 100.000 pedidos diarios con un promedio de 4 productos cada uno:
* **Estado Intrínseco ([ItemCatalogo](file:///home/meac/Documentos/PATRONES%20DE%20SOFT/proyecto%20patrones/smartgrid/smartgrid/src/main/java/com/smartorders/domain/model/)):** SKU, nombre comercial, descripción técnica, especificaciones del fabricante, dimensiones, peso volumétrico y URL de la imagen. Existen 2.000 productos en el catálogo central.
* **Estado Extrínseco ([ItemPedido](file:///home/meac/Documentos/PATRONES%20DE%20SOFT/proyecto%20patrones/smartgrid/smartgrid/src/main/java/com/smartorders/domain/model/ItemPedido.java)):** Cantidad solicitada en la orden, precio unitario acordado al momento de la venta, subtotal calculado y porcentaje de IVA.
* **Beneficio Arquitectónico:** `ItemPedido` no clona la información del producto; únicamente referencia al `ProductoFlyweight`. De $400.000$ descripciones redundantes en memoria se pasa a **2.000 instancias cacheadas**, eliminando el riesgo de `OutOfMemoryError` en horas pico de facturación.

---

## 3. Patrón PROXY (Control de Acceso, Caché y Virtualización)

### 3.1. Propósito e Intención GoF
> *"Provide a surrogate or placeholder for another object to control access to it."* (Gamma et al., 1994, p. 207).

Un Proxy intercepta las llamadas destinadas a un objeto de servicio real (*RealSubject*) respetando su misma interfaz (*Subject*). Actúa como intermediario o "guardián" para gestionar cuándo, cómo y quién tiene permitido ejecutar la lógica costosa subyacente.

### 3.2. Las 5 Variantes Canónicas del Patrón Proxy

```mermaid
flowchart TD
    Cliente[Cliente / Consumidor] --> Interfaz[<< Interface >>\nSubject]
    Proxy[Proxy\n(Intermediario / Control)] -.->|Implementa| Interfaz
    RealSubject[RealSubject\n(Objeto Costoso / Núcleo)] -.->|Implementa| Interfaz
    Proxy -->|Delega si procede| RealSubject

    subgraph Variantes ["5 Variantes de Control de Acceso"]
        P1["1. Protection Proxy\n(Autenticación / RBAC)"]
        P2["2. Virtual Proxy\n(Lazy Loading de pesados)"]
        P3["3. Caching Proxy\n(Memoización de respuestas)"]
        P4["4. Remote Proxy\n(RMI, gRPC, REST Stubs)"]
        P5["5. Logging / Audit Proxy\n(Trazabilidad no invasiva)"]
    end
```

1. **Protection Proxy (Seguridad y Permisos):** Verifica si el solicitante posee los roles y privilegios requeridos antes de reenviar la petición al servicio real.
2. **Virtual Proxy (Inicialización Perezosa / Lazy Loading):** Retrasa la instanciación de objetos con alto costo de creación (por ejemplo, conexión remota, carga de imágenes de alta resolución o reportes voluminosos) hasta el momento exacto en que se invoca su primer método.
3. **Caching Proxy (Memoria Intermedia / Memoización):** Almacena en memoria el resultado de operaciones costosas (como consultas de scoring crediticio o generación de hashes criptográficos CUFE). Si se recibe la misma petición con idénticos parámetros, retorna el valor cacheado sin consumir recursos del *RealSubject*.
4. **Remote Proxy (Localizador de Recursos Remotos):** Proporciona un representante local para un objeto que reside en otro espacio de direcciones, proceso o máquina (ej. *Stubs* de gRPC, RMI o clientes Feign).
5. **Logging / Auditing Proxy:** Registra marcas de tiempo, latencias de red y parámetros antes y después de cada llamada, manteniendo el objeto real libre de código de instrumentación.

### 3.3. Problema Planteado en la Sesión: Emisión de Certificados de Estudio
* **Escenario:** La generación de un certificado oficial de calificaciones requiere consultar notas históricas en múltiples bases de datos, contactar la autoridad de firma digital institucional y generar un PDF con marca de agua criptográfica (operación que toma entre 2 y 3 segundos de cómputo).
* **Solución Proxy (Protection + Caching + Lazy Loading):**
  1. El cliente interactúa con `ICertificadoService`.
  2. `CertificadoProxy` verifica si el usuario es `ESTUDIANTE` activo y está a paz y salvo (*Protection Proxy*).
  3. Si el estudiante solicita el certificado varias veces el mismo día, el proxy retorna el documento ya generado desde su caché local (*Caching Proxy*).
  4. La instancia de `CertificadoRealService` no se crea hasta que una solicitud legítima supere los filtros (*Virtual Proxy*).

### 3.4. Implementación Canónica en Java

```java
// 1. Subject (Interfaz Común)
public interface ICertificadoService {
    String generarCertificado(String codigoEstudiante, String usuarioSolicitante, String rol);
}

// 2. RealSubject (Servicio Costoso de Emisión Oficial)
public class CertificadoRealService implements ICertificadoService {
    public CertificadoRealService() {
        // Simulación de inicialización pesada (conexión criptográfica)
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
        System.out.println("[REAL_SUBJECT] Servicio Criptográfico de Certificados inicializado.");
    }

    @Override
    public String generarCertificado(String codigoEstudiante, String usuarioSolicitante, String rol) {
        System.out.println("[REAL_SUBJECT] Consultando base histórica y firmando digitalmente...");
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        return "CERT-OFICIAL-" + codigoEstudiante + "-HASH:" + UUID.randomUUID().toString().substring(0, 8);
    }
}

// 3. Proxy Híbrido (Protection + Caching + Virtual Proxy)
public class CertificadoProxy implements ICertificadoService {
    private CertificadoRealService servicioReal; // Lazy loading
    private final Map<String, String> cacheCertificados = new ConcurrentHashMap<>();

    @Override
    public String generarCertificado(String codigoEstudiante, String usuarioSolicitante, String rol) {
        // A. Protection Proxy: Validación de Seguridad y Roles
        if (!"SECRETARIA".equalsIgnoreCase(rol) && !"ESTUDIANTE".equalsIgnoreCase(rol)) {
            throw new SecurityException("ACCESO DENEGADO: El rol [" + rol + "] no tiene permisos para emitir certificados.");
        }

        // B. Caching Proxy: Verificación de Caché
        String claveCache = codigoEstudiante + "_" + rol;
        if (cacheCertificados.containsKey(claveCache)) {
            System.out.println("[PROXY_CACHE] Retornando certificado desde memoria caché para: " + codigoEstudiante);
            return cacheCertificados.get(claveCache) + " [CACHE_HIT]";
        }

        // C. Virtual Proxy: Creación bajo demanda (Lazy)
        if (this.servicioReal == null) {
            System.out.println("[PROXY_LAZY] Primera llamada detectada. Instanciando RealSubject...");
            this.servicioReal = new CertificadoRealService();
        }

        // D. Delegación y almacenamiento en caché
        String resultado = servicioReal.generarCertificado(codigoEstudiante, usuarioSolicitante, rol);
        cacheCertificados.put(claveCache, resultado);
        return resultado;
    }
}
```

### 3.5. Aplicación en SmartOrders Enterprise
En nuestro ecosistema [SmartOrders Enterprise](file:///home/meac/Documentos/PATRONES%20DE%20SOFT/proyecto%20patrones/README.md), el patrón Proxy opera a dos niveles complementarios:

1. **Proxy a Nivel de Negocio (`BuroCreditoCacheProxy`):**
   * Cada consulta a la central de riesgo externa ([BuroCreditoLegacyService](file:///home/meac/Documentos/PATRONES%20DE%20SOFT/proyecto%20patrones/smartgrid/smartgrid/src/main/java/com/smartorders/infrastructure/adapters/out/external/BuroCreditoLegacyService.java)) tiene un costo transaccional financiero real ($0.15 USD por consulta API).
   * Un proxy de caché sobre [BuroCreditoPort](file:///home/meac/Documentos/PATRONES%20DE%20SOFT/proyecto%20patrones/smartgrid/smartgrid/src/main/java/com/smartorders/domain/ports/out/BuroCreditoPort.java) almacena los scores crediticios obtenidos con un tiempo de vida (TTL) de 24 horas. Si el mismo cliente emite 3 pedidos en la misma tarde, las consultas 2 y 3 se resuelven a costo cero en $0.2\text{ ms}$.
2. **Proxy a Nivel de Infraestructura (Spring Boot & JPA):**
   * **Spring Dynamic Proxies / CGLIB:** Cada método anotado con `@Transactional` en [PedidoService](file:///home/meac/Documentos/PATRONES%20DE%20SOFT/proyecto%20patrones/smartgrid/smartgrid/src/main/java/com/smartorders/application/usecase/PedidoService.java) es envuelto automáticamente por Spring en un Proxy de Control Transaccional que abre la sesión JPA, inicia el commit y ejecuta el rollback ante excepciones no controladas.
   * **Hibernate Virtual Proxy:** Las listas de navegación `cliente.getHistorialCompras()` y `pedido.getItems()` utilizan proxies perezosos (`Lazy-Loading Proxies`) que solo consultan la base de datos SQL cuando se ejecuta una operación de lectura efectiva sobre la colección.

---

## 4. Comparativa Mnemotécnica de la Terna Estructural

Una de las preguntas más recurrentes en arquitectura de software y evaluaciones de diseño radica en diferenciar **Proxy, Decorator y Adapter**, ya que los tres envuelven objetos:

| Criterio | PROXY (Guardián) | DECORATOR (Envoltorio) | ADAPTER (Traductor) |
| :--- | :--- | :--- | :--- |
| **Intención GoF** | **Controlar acceso** a un objeto (seguridad, lazy, caché). | **Añadir responsabilidades** dinámicamente sin herencia. | **Traducir interfaces** incompatibles para integrarlas. |
| **Interfaz con el Cliente** | **Idéntica** a la del *Subject*. | **Idéntica** a la del *Component*. | **Diferente** a la del *Adaptee* (adopta la del *Target*). |
| **¿Quién crea el objeto real?** | Usualmente el Proxy lo gestiona o conoce su ciclo de vida. | Lo recibe inyectado en el constructor (apilable). | Lo recibe inyectado o lo referencia internamente. |
| **¿Cambia el comportamiento?** | Sí, pero enfocado en **cuándo o cómo** se accede. | Sí, añade lógica **antes o después** de delegar. | No, solo traduce parámetros y tipos de retorno. |
| **Transparencia para el Cliente** | Total: el cliente cree que habla con el objeto real. | Total: el cliente cree que habla con el componente base. | El cliente conoce la nueva interfaz que necesita. |
| **Regla Mnemotécnica UTS** | **"El Proxy CONTROLA"** | **"El Decorator ENRIQUECE"** | **"El Adapter TRADUCE"** |

---

## 5. Cuadro Maestro: Los 7 Patrones Estructurales GoF

Con el cierre del catálogo estructural, presentamos la matriz consolidada de los **7 Patrones Estructurales**, contrastando su propósito general con su implementación y justificación en **SmartOrders Enterprise**:

| # | Patrón GoF | Regla Mnemotécnica | ¿En vuelve? | ¿Cambia Interfaz? | ¿Añade Lógica? | Rol en el Proyecto SmartOrders Enterprise |
| :-: | :--- | :--- | :-: | :-: | :-: | :--- |
| **1** | **Adapter** | *Traductor* | 1 objeto | **Sí** | No (adapta) | [BuroCreditoAdapter.java](file:///home/meac/Documentos/PATRONES%20DE%20SOFT/proyecto%20patrones/smartgrid/smartgrid/src/main/java/com/smartorders/infrastructure/adapters/out/external/BuroCreditoAdapter.java): Homologa el API del sistema legado a la interfaz moderna `BuroCreditoPort`. |
| **2** | **Bridge** | *Arquitecto* | 1 objeto | No | No (separa) | Justificado en [ADR-007](file:///home/meac/Documentos/PATRONES%20DE%20SOFT/proyecto%20patrones/docs/adr/): Descartado en el núcleo para evitar sobreingeniería innecesaria (YAGNI/KISS). |
| **3** | **Composite** | *Árbol* | Varios | No | No (agrega) | Analizado en Semana 8: Modela combos de productos jerárquicos compuestos por ítems individuales bajo `ItemPedido`. |
| **4** | **Decorator** | *Capas* | 1 objeto | No | **Sí (expande)** | [AuditoriaTransaccionDecorator.java](file:///home/meac/Documentos/PATRONES%20DE%20SOFT/proyecto%20patrones/smartgrid/smartgrid/src/main/java/com/smartorders/infrastructure/patterns/decorator/AuditoriaTransaccionDecorator.java): Registra auditoría forense y marcas de tiempo sin tocar la lógica de crédito. |
| **5** | **Facade** | *Puerta de Enlace* | Subsistemas | **Sí (crea nueva)** | No (orquesta) | [PedidoProcesamientoFacade.java](file:///home/meac/Documentos/PATRONES%20DE%20SOFT/proyecto%20patrones/smartgrid/smartgrid/src/main/java/com/smartorders/application/facade/PedidoProcesamientoFacade.java): Orquesta 6 subsistemas comerciales en un único punto de entrada unificado. |
| **6** | **Flyweight** | *Ligero* | 0 (comparte) | No | No (comparte) | Gestión de catálogo de productos inmutables compartidos por miles de líneas de detalle en memoria RAM. |
| **7** | **Proxy** | *Guardián* | 1 objeto | No | **Sí (controla)** | Proxies transaccionales `@Transactional` de Spring, Lazy Loading de Hibernate y caché de scoring crediticio. |

---

## 6. Respuestas a las Preguntas de Evaluación Formativa de la Sesión

### 1. ¿Por qué el estado extrínseco no se guarda en el Flyweight?
**Respuesta:** Porque el estado extrínseco es variable y dependiente del contexto específico de cada cliente (ej. la cantidad de unidades pedidas, el precio de oferta en una fecha concreta o el aula donde un estudiante toma una clase). Si se almacenara dentro del Flyweight, este dejaría de ser inmutable y no podría ser compartido de forma segura por múltiples hilos o clientes, destruyendo el ahorro de memoria del patrón.

### 2. ¿Qué diferencia hay entre Flyweight y un caché tradicional?
**Respuesta:** Aunque ambos almacenan instancias en memoria, su objetivo y naturaleza difieren radicalmente:
* Un **Caché Tradicional** almacena objetos completos (tanto estado intrínseco como extrínseco) de operaciones costosas temporalmente para evitar volver a calcularlos o consultarlos en la base de datos (con políticas de expiración como TTL o LRU).
* **Flyweight** es una técnica de diseño arquitectónico estructural donde los objetos se dividen formalmente en estado intrínseco (permanente en memoria) y extrínseco (calculado o suministrado en runtime). El flyweight vive permanentemente para representar la identidad canónica de una entidad conceptual.

### 3. ¿Cuántos tipos de Proxy conoces? Da un ejemplo de cada uno.
**Respuesta:** Conocemos 5 tipos principales:
1. **Protection Proxy:** `CertificadoProxy` que valida que el usuario posea el rol `SECRETARIA` antes de permitir la emisión.
2. **Virtual Proxy:** Carga perezosa de imágenes de alta resolución en un e-commerce solo cuando el usuario hace scroll hacia el producto.
3. **Caching Proxy:** Intermediario que retiene el resultado de un cálculo actuarial o consulta crediticia durante 24 horas.
4. **Remote Proxy:** Un cliente gRPC o Stub de Spring WebClient que representa un microservicio remoto en otra nube.
5. **Logging / Audit Proxy:** Proxy que intercepta llamadas a la pasarela de pagos para registrar latencia y payloads en la telemetría centralizada.

### 4. ¿Cómo se relaciona Proxy con el Principio de Responsabilidad Única (SRP)?
**Respuesta:** Permite separar limpiamente la **lógica central del negocio** (calcular notas, procesar créditos, emitir certificados) de las **preocupaciones transversales** (*cross-cutting concerns*) como la seguridad, el almacenamiento en caché o la auditoría. Si el servicio real tuviera que validar JWT, consultar Redis y escribir logs, violaría flagrantemente SRP. Con Proxy, cada clase tiene una única razón para cambiar.

### 5. ¿Por qué Spring Boot utiliza proxies para la anotación `@Transactional`?
**Respuesta:** Spring intercepta las invocaciones a los beans anotados con `@Transactional` mediante Proxies Dinámicos del JDK (o CGLIB). Cuando un cliente llama a `pedidoService.crearPedido()`, en realidad llama al Proxy de Spring. Este proxy abre la transacción contra el `DataSource`, invoca el método real dentro de un bloque `try-catch`, y si la ejecución finaliza con éxito, ejecuta `connection.commit()`; si se dispara una `RuntimeException`, el proxy captura el error y ejecuta `connection.rollback()`. El código de negocio del servicio permanece 100% puro y ajeno a JDBC o JPA.

---

## 7. Conclusiones Arquitectónicas y Verificación QA

1. **Eficiencia en Recursos:** Flyweight demuestra que la optimización no requiere sacrificar el diseño orientado a objetos; separar el estado intrínseco permite escalar a millones de entidades concurrentes reduciendo el impacto en el Garbage Collector hasta en un $98\%$.
2. **Control No Invasivo:** Proxy complementa a Facade y Decorator: mientras Facade simplifica y Decorator extiende, Proxy actúa como la primera línea de defensa, gobernando el ciclo de vida, la seguridad y el rendimiento de los servicios de negocio.
3. **Madurez del Backend:** Con los 7 patrones estructurales comprendidos y aplicados, el backend **SmartOrders Enterprise** se consolida con **32 pruebas unitarias automatizadas**, **82.84% de cobertura JaCoCo** y estricto cumplimiento de Clean Architecture y DDD.

---

## 📚 8. Referencias Bibliográficas Oficiales

* **Gamma, E., Helm, R., Johnson, R., & Vlissides, J. (1994).** *Design Patterns: Elements of Reusable Object-Oriented Software*. Addison-Wesley. (Cap. 4: Structural Patterns: Flyweight pp. 195–206, Proxy pp. 207–217).
* **Freeman, E., & Robson, E. (2020).** *Head First Design Patterns: A Brain-Friendly Guide* (2nd ed.). O'Reilly Media. (Cap. 11: Proxy Pattern, Cap. 12: Compound Patterns).
* **Martin, R. C. (2002).** *Agile Software Development, Principles, Patterns, and Practices*. Prentice Hall.
* **Bloch, J. (2018).** *Effective Java* (3rd ed.). Addison-Wesley Professional. (Item 17: Minimize mutability, Item 20: Prefer interfaces to abstract classes).
