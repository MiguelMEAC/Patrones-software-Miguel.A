package com.smartorders.domain.model;

/**
 * Value Object que encapsula los datos del perfil del cliente.
 */
public class Perfil {

    private final int edad;
    private final String ocupacion;
    private final String telefono;

    public Perfil(int edad, String ocupacion, String telefono) {
        this.edad = edad;
        this.ocupacion = ocupacion != null ? ocupacion : "No especificada";
        this.telefono = telefono != null ? telefono : "";
    }

    public int getEdad() {
        return edad;
    }

    public String getOcupacion() {
        return ocupacion;
    }

    public String getTelefono() {
        return telefono;
    }
}
