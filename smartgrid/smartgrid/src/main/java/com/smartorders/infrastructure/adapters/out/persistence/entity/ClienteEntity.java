package com.smartorders.infrastructure.adapters.out.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "clientes")
public class ClienteEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String nombre;

    private String email;

    private double limiteCredito;

    private int edad;
    private String ocupacion;
    private String telefono;

    private int estrato;
    private String nitORut;
    private String regimenFiscal;

    private int comprasHistoricasCount;
    private boolean pagosAlDia;

    public ClienteEntity() {}

    public ClienteEntity(String id, String nombre, String email, double limiteCredito,
                         int edad, String ocupacion, String telefono,
                         int estrato, String nitORut, String regimenFiscal,
                         int comprasHistoricasCount, boolean pagosAlDia) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.limiteCredito = limiteCredito;
        this.edad = edad;
        this.ocupacion = ocupacion;
        this.telefono = telefono;
        this.estrato = estrato;
        this.nitORut = nitORut;
        this.regimenFiscal = regimenFiscal;
        this.comprasHistoricasCount = comprasHistoricasCount;
        this.pagosAlDia = pagosAlDia;
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public double getLimiteCredito() { return limiteCredito; }
    public void setLimiteCredito(double limiteCredito) { this.limiteCredito = limiteCredito; }

    public int getEdad() { return edad; }
    public void setEdad(int edad) { this.edad = edad; }

    public String getOcupacion() { return ocupacion; }
    public void setOcupacion(String ocupacion) { this.ocupacion = ocupacion; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public int getEstrato() { return estrato; }
    public void setEstrato(int estrato) { this.estrato = estrato; }

    public String getNitORut() { return nitORut; }
    public void setNitORut(String nitORut) { this.nitORut = nitORut; }

    public String getRegimenFiscal() { return regimenFiscal; }
    public void setRegimenFiscal(String regimenFiscal) { this.regimenFiscal = regimenFiscal; }

    public int getComprasHistoricasCount() { return comprasHistoricasCount; }
    public void setComprasHistoricasCount(int comprasHistoricasCount) { this.comprasHistoricasCount = comprasHistoricasCount; }

    public boolean isPagosAlDia() { return pagosAlDia; }
    public void setPagosAlDia(boolean pagosAlDia) { this.pagosAlDia = pagosAlDia; }
}
