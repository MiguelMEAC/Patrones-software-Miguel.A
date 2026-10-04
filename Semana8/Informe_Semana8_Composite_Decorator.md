# 📑 INFORME TÉCNICO - SEMANA 8: PATRONES ESTRUCTURALES COMPOSITE & DECORATOR
## Curso: Patrones de Diseño de Software (PIS1003) — Corte 2 (Segundo Corte)
### Metodología: Aprendizaje Basado en Proyectos (ABPr) — Unidades Tecnológicas de Santander (UTS)
* **Estudiante:** Miguel Eduardo Ardila Cossio (`meduardoardila@uts.edu.co`)
* **Docente:** Eliecer Montero Ojeda Ed.D
* **Fecha de Entrega Oficial:** 04/10/2026
* **Dominio:** Sistema General Académico & SmartOrders Enterprise

---

## 1. Conexión con lo Aprendido y Ubicación en el Catálogo GoF

Los patrones estructurales resuelven cómo organizar y conectar clases y objetos para que colaboren eficientemente en estructuras más ricas y flexibles:
* **Con los Creacionales (Corte 1):** Singleton garantiza instancia única, Factory Method delega la instanciación en subclases, y Builder construye objetos complejos paso a paso. Composite y Decorator resuelven cómo componer y enriquecer esos objetos ya creados.
* **Con Adapter y Bridge (Semana 7):** Mientras Adapter traduce interfaces incompatibles y Bridge separa dos dimensiones ortogonales para evitar la explosión combinatoria, **Composite y Decorator abordan dos problemas estructurales esenciales**:
  1. **Jerarquías recursivas:** Tratar colecciones de objetos y objetos individuales de forma idéntica sin condicionales (*Composite*).
  2. **Extensiones dinámicas:** Añadir funcionalidades o responsabilidades en runtime sin incurrir en $2^n$ combinaciones de subclases por herencia (*Decorator*).

---

## 2. Patrón COMPOSITE (Composición de Jerarquías en Árbol)

### 2.1. Propósito e Intención GoF
> *"Compose objects into tree structures to represent part-whole hierarchies. Composite lets clients treat individual objects and compositions of objects uniformly."*

Composite permite construir estructuras jerárquicas en árbol (parte-todo) donde los clientes pueden tratar de forma indistinta y uniforme a los objetos individuales (hojas o *Leaf*) y a las agrupaciones o ramas compuestas (*Composite*), delegando las operaciones recursivamente.

### 2.2. Motivación: La Jerarquía Académica
En un Sistema de Gestión Académica, la estructura organizativa es naturalmente un árbol:
```text
Facultad de Ingenierías (Composite)
 ├── Programa de Ingeniería de Sistemas (Composite)
 │    ├── Curso de Patrones de Diseño (Composite)
 │    │    ├── Estudiante Ana (Leaf - Promedio: 4.5)
 │    │    └── Estudiante Luis (Leaf - Promedio: 3.8)
 │    └── Curso de Bases de Datos (Composite)
 │         ├── Estudiante Ana (Leaf - Promedio: 4.5)
 │         └── Estudiante Pedro (Leaf - Promedio: 4.1)
 └── Programa de Ingeniería Industrial (Composite)
      └── Curso de Procesos (Composite)
           └── Estudiante Carla (Leaf - Promedio: 4.0)
```
**El Reto:** ¿Cómo calcular el promedio académico o el total de estudiantes de una Facultad entera sin escribir un código plagado de `if (esFacultad) ... else if (esPrograma) ... else if (esCurso)`?

**La Solución Composite:** Se define la interfaz común `ElementoAcademico` con los métodos `getPromedio()`, `getTotalEstudiantes()` y `mostrar()`. Tanto las hojas (`Estudiante`) como los composites (`Curso`, `Programa`, `Facultad`) la implementan, logrando que el cliente invoque la operación de forma transparente.

### 2.3. Implementación Canónica en Java

#### Component (Interfaz Común):
```java
package co.uts.academico.dominio;

public interface ElementoAcademico {
    double getPromedio();
    int getTotalEstudiantes();
    void mostrar(String indentacion);
}
```

#### Leaf (Hoja - Objeto Individual Sin Hijos):
```java
package co.uts.academico.dominio;

public class Estudiante implements ElementoAcademico {
    private final String nombre;
    private final double promedio;

    public Estudiante(String nombre, double promedio) {
        this.nombre = nombre;
        this.promedio = promedio;
    }

    @Override
    public double getPromedio() {
        return promedio;
    }

    @Override
    public int getTotalEstudiantes() {
        return 1;
    }

    @Override
    public void mostrar(String ind) {
        System.out.printf("%s- Estudiante: %s (Promedio: %.2f)%n", ind, nombre, promedio);
    }
}
```

#### Composite (Ramas - Contenedor con Hijos):
```java
package co.uts.academico.dominio;

import java.util.ArrayList;
import java.util.List;

public class CompositeAcademico implements ElementoAcademico {
    private final String nombre;
    private final String tipo; // "Facultad", "Programa" o "Curso"
    private final List<ElementoAcademico> hijos = new ArrayList<>();

    public CompositeAcademico(String tipo, String nombre) {
        this.tipo = tipo;
        this.nombre = nombre;
    }

    public void agregar(ElementoAcademico elemento) {
        hijos.add(elemento);
    }

    public void eliminar(ElementoAcademico elemento) {
        hijos.remove(elemento);
    }

    @Override
    public double getPromedio() {
        return hijos.stream()
                .mapToDouble(ElementoAcademico::getPromedio)
                .average()
                .orElse(0.0);
    }

    @Override
    public int getTotalEstudiantes() {
        return hijos.stream()
                .mapToInt(ElementoAcademico::getTotalEstudiantes)
                .sum();
    }

    @Override
    public void mostrar(String ind) {
        System.out.println(ind + "+ " + tipo + ": " + nombre);
        hijos.forEach(h -> h.mostrar(ind + "  "));
    }
}
```

#### Cliente Uniforme:
```java
public class ClienteAcademicoDemo {
    public static void main(String[] args) {
        // Hojas
        Estudiante ana = new Estudiante("Ana Gomez", 4.5);
        Estudiante luis = new Estudiante("Luis Perez", 3.8);
        Estudiante pedro = new Estudiante("Pedro Diaz", 4.1);

        // Composites
        CompositeAcademico cursoPatrones = new CompositeAcademico("Curso", "Patrones de Diseño");
        cursoPatrones.agregar(ana);
        cursoPatrones.agregar(luis);

        CompositeAcademico cursoBD = new CompositeAcademico("Curso", "Bases de Datos");
        cursoBD.agregar(pedro);

        CompositeAcademico progSistemas = new CompositeAcademico("Programa", "Ing. Sistemas");
        progSistemas.agregar(cursoPatrones);
        progSistemas.agregar(cursoBD);

        CompositeAcademico facultadIng = new CompositeAcademico("Facultad", "Ingenierías");
        facultadIng.agregar(progSistemas);

        // El cliente trata por igual a un estudiante o a una facultad entera:
        System.out.println("Promedio Facultad: " + facultadIng.getPromedio());
        System.out.println("Total Alumnos Facultad: " + facultadIng.getTotalEstudiantes());
        facultadIng.mostrar("");
    }
}
```

### 2.4. Análisis de Aplicabilidad en SmartOrders Enterprise
* **En el Módulo Central de Pedidos:** Se evaluó y descartó en las órdenes de compra simples porque la relación entre un pedido y sus líneas de detalle es lineal y plana (`1 Pedido -> N ItemPedido`), sin recursión jerárquica.
* **En el Módulo de Paquetes y Combos Comerciales:** Composite es la solución idónea para modelar **Kits o Combos Promocionales**, donde un producto en el carrito puede ser un producto individual o un paquete que agrupa otros sub-productos (ej. `Combo Oficina = Laptop + Kit Periféricos [Teclado + Mouse]`), calculando el subtotal y peso de manera recursiva uniforme.

---

## 3. Patrón DECORATOR (Extensiones Dinámicas sin Herencia)

### 3.1. Propósito e Intención GoF
> *"Attach additional responsibilities to an object dynamically. Decorators provide a flexible alternative to subclassing for extending functionality."*

Decorator permite envolver un objeto base en uno o más objetos decoradores que comparten la misma interfaz, agregando comportamiento antes o después de delegar la llamada, sin alterar la clase original ni recurrir a la herencia rígida.

### 3.2. Motivación: El Cálculo de la Matrícula Académica
El costo de matrícula de un estudiante en la universidad puede verse afectado por múltiples factores combinables en tiempo de ejecución:
* Beca por mérito académico (-30%).
* Descuento por hermanos (-10%).
* Descuento por pronto pago (-5%).
* Recargo por curso intersemestral (+15%).
* Recargo por seguro médico estudiantil (+$50.000 COP).

Si modeláramos esto con herencia pura, se produciría una **explosión combinatoria de $2^5 = 32$ subclases** (`EstudianteConBeca`, `EstudianteConBecaYHermanos`, `EstudianteConBecaYHermanosYProntoPago`, etc.). Con Decorator, simplemente se crean **5 decoradores independientes** que se anidan en runtime según el caso específico.

### 3.3. Implementación Canónica en Java

#### Component:
```java
package co.uts.academico.matricula;

public interface CalculoMatricula {
    double calcular();
    String getDetalle();
}
```

#### ConcreteComponent:
```java
package co.uts.academico.matricula;

public class MatriculaBase implements CalculoMatricula {
    private final double costoBase;

    public MatriculaBase(double costoBase) {
        this.costoBase = costoBase;
    }

    @Override
    public double calcular() {
        return costoBase;
    }

    @Override
    public String getDetalle() {
        return String.format("Matricula Base: $%,.2f", costoBase);
    }
}
```

#### Decorator Abstracto:
```java
package co.uts.academico.matricula;

public abstract class MatriculaDecorator implements CalculoMatricula {
    protected final CalculoMatricula componente;

    public MatriculaDecorator(CalculoMatricula componente) {
        this.componente = componente;
    }

    @Override
    public double calcular() {
        return componente.calcular();
    }

    @Override
    public String getDetalle() {
        return componente.getDetalle();
    }
}
```

#### ConcreteDecorators:
```java
package co.uts.academico.matricula;

// Decorador 1: Beca Mérito (-30%)
public class BecaMeritoDecorator extends MatriculaDecorator {
    public BecaMeritoDecorator(CalculoMatricula componente) {
        super(componente);
    }

    @Override
    public double calcular() {
        return super.calcular() * 0.70;
    }

    @Override
    public String getDetalle() {
        return super.getDetalle() + " + Beca Merito (-30%)";
    }
}

// Decorador 2: Descuento Hermanos (-10%)
public class DescuentoHermanosDecorator extends MatriculaDecorator {
    public DescuentoHermanosDecorator(CalculoMatricula componente) {
        super(componente);
    }

    @Override
    public double calcular() {
        return super.calcular() * 0.90;
    }

    @Override
    public String getDetalle() {
        return super.getDetalle() + " + Descuento Hermanos (-10%)";
    }
}

// Decorador 3: Recargo Intersemestral (+15%)
public class RecargoIntersemestralDecorator extends MatriculaDecorator {
    public RecargoIntersemestralDecorator(CalculoMatricula componente) {
        super(componente);
    }

    @Override
    public double calcular() {
        return super.calcular() * 1.15;
    }

    @Override
    public String getDetalle() {
        return super.getDetalle() + " + Recargo Intersemestral (+15%)";
    }
}
```

#### Cliente Dinámico:
```java
public class LiquidacionMatriculaDemo {
    public static void main(String[] args) {
        // Estudiante A: Matrícula Base + Beca Mérito + Descuento Hermanos
        CalculoMatricula m1 = new MatriculaBase(5000000.0);
        m1 = new BecaMeritoDecorator(m1);
        m1 = new DescuentoHermanosDecorator(m1);

        System.out.println("Estudiante A: " + m1.getDetalle());
        System.out.printf("Total a Pagar: $%,.2f%n%n", m1.calcular());

        // Estudiante B: Matrícula Base + Recargo Intersemestral
        CalculoMatricula m2 = new RecargoIntersemestralDecorator(new MatriculaBase(5000000.0));
        System.out.println("Estudiante B: " + m2.getDetalle());
        System.out.printf("Total a Pagar: $%,.2f%n", m2.calcular());
    }
}
```

### 3.4. Implementación en SmartOrders Enterprise: `AuditoriaTransaccionDecorator`
En nuestro proyecto empresarial **SmartOrders**, el patrón Decorator está **implementado y operativo al 100%**:
* **Componente Base:** `TransaccionCreditoBase.java` ejecuta la evaluación de solvencia crediticia.
* **Decorador:** `AuditoriaTransaccionDecorator.java` envuelve la transacción mediante Spring (`@Primary`), inyectando de forma transparente marcas de tiempo, trazas estructuradas `[AUDITORIA_TRANSACCION]` y alertas de riesgo financiero sin contaminar la lógica pura del negocio.

---

## 4. Comparativa Técnica Exhaustiva: COMPOSITE vs. DECORATOR

| Criterio | Patrón COMPOSITE | Patrón DECORATOR |
| :--- | :--- | :--- |
| **Intención Principal** | Tratar uniformemente hojas individuales y composiciones. | Añadir responsabilidades dinámicamente en tiempo de ejecución. |
| **Estructura** | Árbol recursivo (`1..*` hijos). | Cadena o envoltura lineal (`1` componente envuelto). |
| **Número de Hijos** | Muchos (composición/agregación). | Uno (composición por envoltura). |
| **Relación con Hijos** | *"Tiene muchos"* (`hijos.add(e)`). | *"Envuelve a uno"* (`super(componente)`). |
| **Objetivo** | Uniformidad y agregación de operaciones. | Extensión y enriquecimiento de comportamiento. |
| **¿Modifica la Interfaz?** | No, la conserva estrictamente. | No, la conserva estrictamente. |
| **Alternativa a** | Jerarquía rígida con condicionales `if/else` por tipo. | Explosión combinatoria de $2^n$ subclases por herencia. |
| **Visión del Cliente** | Un árbol transparente tratado como un único objeto. | Un objeto enriquecido transparente. |

---

## 5. El Cuarteto Estructural Completo: Adapter, Bridge, Composite y Decorator

| Patrón | ¿Envuelve? | ¿A cuántos? | ¿Cambia Interfaz? | ¿Añade Comportamiento? | Regla Mnemotécnica |
| :--- | :---: | :---: | :---: | :---: | :--- |
| **Adapter** | Sí | 1 | **Sí (traduce)** | No (solo adapta) | *Traductor* |
| **Bridge** | Sí (ref.) | 1 | No | No (delega) | *Arquitecto* |
| **Composite** | Sí | **Muchos** | No | No (agrega recursivo) | *Árbol* |
| **Decorator** | Sí | 1 | No | **Sí (enriquece)** | *Capas* |

---

## 6. Respuestas a las Preguntas de Reflexión de la Cátedra (Diapositiva 18)

1. **¿Por qué Composite es útil cuando la jerarquía es natural y Decorator cuando la extensión es dinámica?**  
   Porque Composite refleja estructuras estables de pertenencia (un curso siempre pertenece a un programa y este a una facultad), permitiendo navegar el árbol con recursión natural. Por el contrario, Decorator no modela pertenencia sino atributos volátiles que un objeto puede ganar o perder en caliente (un estudiante hoy tiene beca, el próximo semestre no).

2. **¿En qué casos preferirías herencia tradicional sobre Composite o Decorator?**  
   Cuando la variación es fija, pequeña, conocida en tiempo de diseño y no existe riesgo de combinatoria (por ejemplo, `EmpleadoAsalariado` vs `EmpleadoPorHoras`).

3. **¿Qué pasaría si en el cálculo de matrícula aplicaras primero `RecargoIntersemestral (+15%)` y luego `BecaMerito (-30%)` vs. al revés?**  
   En operaciones multiplicativas de porcentajes directos el producto es conmutativo: $1.15 \times 0.70 = 0.805$. Sin embargo, si existen **recargos fijos** (ej. carnets por \$50.000 COP) y la beca solo aplica sobre el costo académico neto, **el orden de la cadena de decoradores altera drásticamente el resultado financiero**. Por ello, el orden debe documentarse y validarse formalmente.

4. **¿Puede un Composite también ser un Decorator?**  
   Sí, teóricamente es posible aunque poco frecuente: un decorador puede envolver a un componente que internamente es la raíz de un Composite, decorando la operación agregada de todo el árbol.

5. **¿Qué relación tienen ambos patrones con los principios SOLID?**  
   * **Open/Closed Principle (OCP):** Nuevas hojas/ramas en Composite o nuevos decoradores se agregan sin modificar una sola línea del código cliente.  
   * **Single Responsibility Principle (SRP):** Cada decorador se enfoca en una única responsabilidad (solo beca, solo recargo).  
   * **Liskov Substitution Principle (LSP):** Cualquier rama o decorador puede sustituir al componente base sin quebrar el sistema.

---

## 7. Conclusión y Verificación del Proyecto
* **Decorator en Producción:** `AuditoriaTransaccionDecorator` validado con JUnit y cobertura del 100% en `smartorders`.
* **Composite Documentado y Modelado:** Arquitectura de jerarquías académicas y kits comerciales documentada formalmente para el Corte 2.
* **Integridad Global:** 32 pruebas pasando, 82.84% de cobertura JaCoCo y repositorio GitHub sincronizado.
