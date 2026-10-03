package com.smartorders.domain.model;

/**
 * Value Object que encapsula informacion socioeconomica y fiscal para subsidios y facturacion.
 */
public class DatosFiscales {

    private final int estrato;
    private final String nitORut;
    private final String regimenFiscal;

    public DatosFiscales(int estrato, String nitORut, String regimenFiscal) {
        this.estrato = estrato;
        this.nitORut = nitORut != null ? nitORut : "Consumidor Final";
        this.regimenFiscal = regimenFiscal != null ? regimenFiscal : "Comun";
    }

    public int getEstrato() {
        return estrato;
    }

    public String getNitORut() {
        return nitORut;
    }

    public String getRegimenFiscal() {
        return regimenFiscal;
    }
}
