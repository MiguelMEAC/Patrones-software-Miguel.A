package Semana1.CodigoInicialLegacy;

import java.util.Date;
import java.util.List;

public class Cliente {
    private List<Compra> historialCompras;
    private Perfil perfil;
    private DatosFiscales datosFiscales;
    private List<Pago> historialPagos;
    private double limiteCredito;

    public List<Compra> getHistorialCompras() { return historialCompras; }
    public void setHistorialCompras(List<Compra> historialCompras) { this.historialCompras = historialCompras; }

    public Perfil getPerfil() { return perfil; }
    public void setPerfil(Perfil perfil) { this.perfil = perfil; }

    public DatosFiscales getDatosFiscales() { return datosFiscales; }
    public void setDatosFiscales(DatosFiscales datosFiscales) { this.datosFiscales = datosFiscales; }

    public List<Pago> getHistorialPagos() { return historialPagos; }
    public void setHistorialPagos(List<Pago> historialPagos) { this.historialPagos = historialPagos; }

    public double getLimiteCredito() { return limiteCredito; }
    public void setLimiteCredito(double limiteCredito) { this.limiteCredito = limiteCredito; }
}

class Compra {
    private Date fecha;
    private double monto;

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }

    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }
}

class Perfil {
    private int edad;

    public int getEdad() { return edad; }
    public void setEdad(int edad) { this.edad = edad; }
}

class DatosFiscales {
    private int estrato;

    public int getEstrato() { return estrato; }
    public void setEstrato(int estrato) { this.estrato = estrato; }
}

class Pago {
    private boolean pagado;
    private double monto;

    public boolean isPagado() { return pagado; }
    public void setPagado(boolean pagado) { this.pagado = pagado; }

    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }
}
