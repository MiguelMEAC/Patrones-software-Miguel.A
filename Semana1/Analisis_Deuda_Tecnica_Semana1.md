# 📑 INFORME TÉCNICO - SEMANA 1: DIAGNÓSTICO Y ANÁLISIS DE DEUDA TÉCNICA
## Proyecto: SmartOrders Enterprise
### Contexto Inicial: Módulo Legacy de Pedido, Cliente y Evaluación Crediticia

---

### 1. Planteamiento del Problema
El sistema inicial fue concebido con un modelo anémico y altamente acoplado. El archivo `Pedido.java` concentra lógica de cálculo comercial, navegación por estructuras internas del cliente, manejo obsoleto de fechas y evaluación crediticia rudimentaria.

```java
// Fragmento recibido en Semana 1:
public class Pedido {
    private Cliente cliente;

    public double calcularDescuento() {
        if (cliente.getHistorialCompras().size() > 10) {
            return 0.15; // VIP
        }
        if (cliente.getHistorialCompras().stream()
                .filter(c -> c.getFecha().after(nuevaFecha(-365)))
                .count() > 5) {
            return 0.10; // Frecuente
        }
        if (cliente.getPerfil().getEdad() > 65) {
            return 0.05; // Senior
        }
        if (cliente.getDatosFiscales().getEstrato() < 2) {
            return 0.20; // Subsidio
        }
        return 0;
    }

    private Date nuevaFecha(int dias) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, dias);
        return cal.getTime();
    }

    public boolean validarCredito() {
        return cliente.getHistorialPagos().stream()
                .allMatch(p -> p.isPagado() && 
                        p.getMonto() <= cliente.getLimiteCredito());
    }
}

class Cliente {
    private List<Compra> historialCompras;
    private Perfil perfil;
    private DatosFiscales datosFiscales;
    private List<Pago> historialPagos;
}
```

---

### 2. Violaciones de Principios de Ingeniería de Software (SOLID)

1. **Violación del Principio Abierto/Cerrado (OCP - Open/Closed Principle):**
   - El método `calcularDescuento()` posee condicionales `if-else` quemados. Para añadir un nuevo descuento (ej. "Convenio Estudiantil" o "Black Friday"), es forzoso modificar el código fuente de `Pedido`, aumentando el riesgo de regresiones.
2. **Violación del Principio de Responsabilidad Única (SRP - Single Responsibility Principle):**
   - `Pedido` es responsable de:
     - Almacenar items del pedido.
     - Conocer reglas de marketing y descuentos.
     - Operar aritmética de fechas mediante `Calendar`.
     - Validar la solvencia y riesgo crediticio del cliente.
3. **Violación de la Ley de Demeter (*Principio del Mínimo Conocimiento*):**
   - Invocaciones encadenadas como `cliente.getPerfil().getEdad()` y `cliente.getDatosFiscales().getEstrato()` demuestran un acoplamiento indebido (*Feature Envy*). `Pedido` no debería conocer los detalles internos de cómo un cliente organiza sus datos fiscales.
4. **Obsolescencia Técnica en Manejo de Fechas:**
   - Empleo de `java.util.Date` y `java.util.Calendar`, clases mutables y propensas a errores de concurrencia y zonas horarias, en lugar del estándar inmutable `java.time.LocalDate` (Java 8+).

---

### 3. Hoja de Ruta de Refactorización (Evolución Semanal)
Para erradicar la deuda técnica y cumplir la rúbrica de la UTS, se define el siguiente plan:
- **Semana 3:** Implementación del patrón **Singleton** para centralizar la configuración del motor de órdenes.
- **Semana 4:** Implementación del patrón **Factory Method** para desacoplar la facturación comercial de la electrónica.
- **Semana 5:** Implementación del patrón **Builder** para la creación inmutable y segura de pedidos complejos.
- **Semana 6:** Implementación de patrones estructurales (**Adapter** y **Decorator**) para integrar burós de crédito externos y registrar auditoría.
- **Semana 7:** Implementación de patrones de comportamiento (**Strategy**, **Observer**, **State**) y empaquetado en **Arquitectura Hexagonal**.
