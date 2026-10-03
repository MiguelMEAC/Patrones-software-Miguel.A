package com.smartgrid.smartgrid.config;

public class SmartGridConfig {

    private static SmartGridConfig instancia; //Variable estatica donde se almacena la intancia

    private String nombreSistema;
    private boolean sistemaActivo;

    private SmartGridConfig() {
        this.nombreSistema = "SmartGrid"; //constructor privado
        this.sistemaActivo = true;
    }

    public static SmartGridConfig getInstancia() { //metodo para obtener la instancia

        if (instancia == null) {
            instancia = new SmartGridConfig();
        }

        return instancia;
    }

    public String getNombreSistema() {
        return nombreSistema;
    }

    public boolean isSistemaActivo() {
        return sistemaActivo;
    }

    public void activarSistema() {
        sistemaActivo = true;
    }

    public void desactivarSistema() {
        sistemaActivo = false;
    }
}