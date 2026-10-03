package com.smartorders.infrastructure.config;

/**
 * Patrón Creacional: Singleton (GoF)
 * Garantiza una única instancia centralizada de la configuración del motor de pedidos y reglas financieras.
 * Implementación Thread-Safe con Double-Checked Locking.
 */
public class SistemaConfig {

    private static volatile SistemaConfig instancia;

    private String nombreSistema;
    private boolean sistemaActivo;
    private double tasaIva;
    private double limiteMaximoCreditoPorDefecto;
    private boolean permitirVentaSinStock;

    // Constructor privado para evitar instanciacion externa directa
    private SistemaConfig() {
        this.nombreSistema = "SmartOrders Engine Enterprise";
        this.sistemaActivo = true;
        this.tasaIva = 0.19; // 19%
        this.limiteMaximoCreditoPorDefecto = 5000000.0;
        this.permitirVentaSinStock = false;
    }

    public static SistemaConfig getInstancia() {
        if (instancia == null) {
            synchronized (SistemaConfig.class) {
                if (instancia == null) {
                    instancia = new SistemaConfig();
                }
            }
        }
        return instancia;
    }

    // Metodo para reiniciar estado en pruebas unitarias si fuese necesario
    public static synchronized void resetParaPruebas() {
        instancia = null;
    }

    public String getNombreSistema() {
        return nombreSistema;
    }

    public void setNombreSistema(String nombreSistema) {
        this.nombreSistema = nombreSistema;
    }

    public boolean isSistemaActivo() {
        return sistemaActivo;
    }

    public void setSistemaActivo(boolean sistemaActivo) {
        this.sistemaActivo = sistemaActivo;
    }

    public double getTasaIva() {
        return tasaIva;
    }

    public void setTasaIva(double tasaIva) {
        this.tasaIva = tasaIva;
    }

    public double getLimiteMaximoCreditoPorDefecto() {
        return limiteMaximoCreditoPorDefecto;
    }

    public void setLimiteMaximoCreditoPorDefecto(double limiteMaximoCreditoPorDefecto) {
        this.limiteMaximoCreditoPorDefecto = limiteMaximoCreditoPorDefecto;
    }

    public boolean isPermitirVentaSinStock() {
        return permitirVentaSinStock;
    }

    public void setPermitirVentaSinStock(boolean permitirVentaSinStock) {
        this.permitirVentaSinStock = permitirVentaSinStock;
    }
}
