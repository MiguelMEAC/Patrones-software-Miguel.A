package com.smartorders.infrastructure.adapters.out.persistence;

import com.smartorders.domain.model.Cliente;
import com.smartorders.domain.model.Compra;
import com.smartorders.domain.model.DatosFiscales;
import com.smartorders.domain.model.Pago;
import com.smartorders.domain.model.Perfil;
import com.smartorders.domain.ports.out.ClienteRepositoryPort;
import com.smartorders.infrastructure.adapters.out.persistence.entity.ClienteEntity;
import com.smartorders.infrastructure.adapters.out.persistence.repository.SpringDataClienteRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class ClientePersistenceAdapter implements ClienteRepositoryPort {

    private final SpringDataClienteRepository repository;

    public ClientePersistenceAdapter(SpringDataClienteRepository repository) {
        this.repository = repository;
    }

    @Override
    public Cliente guardar(Cliente cliente) {
        ClienteEntity entity = toEntity(cliente);
        ClienteEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Cliente> buscarPorId(String id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Cliente> listarTodos() {
        return repository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    public ClienteEntity toEntity(Cliente domain) {
        return new ClienteEntity(
                domain.getId(),
                domain.getNombre(),
                domain.getEmail(),
                domain.getLimiteCredito(),
                domain.getPerfil().getEdad(),
                domain.getPerfil().getOcupacion(),
                domain.getPerfil().getTelefono(),
                domain.getDatosFiscales().getEstrato(),
                domain.getDatosFiscales().getNitORut(),
                domain.getDatosFiscales().getRegimenFiscal(),
                domain.getHistorialCompras().size(),
                domain.validarHistorialCrediticioInterno()
        );
    }

    public Cliente toDomain(ClienteEntity entity) {
        Perfil perfil = new Perfil(entity.getEdad(), entity.getOcupacion(), entity.getTelefono());
        DatosFiscales datosFiscales = new DatosFiscales(entity.getEstrato(), entity.getNitORut(), entity.getRegimenFiscal());

        List<Compra> compras = new ArrayList<>();
        // Reconstruccion de historial simulado para evaluacion de estrategias
        for (int i = 0; i < entity.getComprasHistoricasCount(); i++) {
            compras.add(new Compra("CMP-" + i, LocalDate.now().minusDays(i * 15), 100000.0, "Compra historica #" + i));
        }

        List<Pago> pagos = new ArrayList<>();
        pagos.add(new Pago("PAG-1", LocalDate.now().minusDays(30), 50000.0, entity.isPagosAlDia()));

        return new Cliente(
                entity.getId(),
                entity.getNombre(),
                entity.getEmail(),
                entity.getLimiteCredito(),
                perfil,
                datosFiscales,
                compras,
                pagos
        );
    }
}
