# ADR-002: Implementación de Patrones Creacionales (Singleton, Factory Method, Builder)

## Estado
**Aceptado**

## Contexto
El sistema requiere gestionar la configuración central de la plataforma, generar diferentes tipos de facturación tributaria y construir pedidos complejos garantizando invariantes de negocio e inmutabilidad.

## Decisiones de Diseño

### 1. Patrón Singleton (`SistemaConfig`)
- **Problema:** Múltiples componentes requerían conocer y modificar el estado operativo (modo activo o mantenimiento), tasa de IVA y límites globales de crédito.
- **Solución:** Implementación de `SistemaConfig` con constructor privado, variable volátil y sincronización *Double-Checked Locking* para garantizar unicidad y thread-safety.

### 2. Patrón Factory Method (`FacturaFactory`)
- **Problema:** La lógica de emisión de facturas requería desacoplar la creación del documento (`FacturaEstandar` vs `FacturaElectronicaFiscal` con CUFE) de los controladores y servicios.
- **Solución:** Clase abstracta `FacturaFactory` con el método abstracto `crearFactura()`. Las subclases concretas (`FacturaEstandarFactory`, `FacturaElectronicaFiscalFactory`) determinan qué producto instanciar.

### 3. Patrón Builder (`PedidoBuilder`)
- **Problema:** `Pedido` contiene múltiples atributos (obligatorios como cliente e items, y opcionales como descuentos, observaciones, fecha personalizada). Usar constructores sobrecargados genera código frágil (*Telescoping Constructor Anti-Pattern*).
- **Solución:** `PedidoBuilder` exige en su constructor al cliente, valida que existan items antes de llamar a `build()` y calcula los totales financieros de forma inmutable y defensiva.

## Patrones Descartados
- **Abstract Factory:** Descartado porque el sistema no requiere crear familias completas e interdependientes de objetos en bloque (la facturación y los pedidos evolucionan de forma independiente).
- **Prototype:** Descartado debido a que la API REST opera sin estado (*stateless*) y cada pedido se construye dinámicamente a partir del payload JSON, haciendo redundante clonar instancias en memoria.
