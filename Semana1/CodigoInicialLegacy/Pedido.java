package Semana1.CodigoInicialLegacy;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * CODIGO INICIAL LEGACY (Semana 1):
 * Código suministrado con deuda técnica, violaciones de principios SOLID (OCP, SRP, Demeter)
 * y tipos temporales obsoletos (Date, Calendar).
 */
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

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
}
