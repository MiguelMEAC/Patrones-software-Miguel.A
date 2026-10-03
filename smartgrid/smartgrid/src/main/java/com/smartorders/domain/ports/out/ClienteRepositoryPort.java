package com.smartorders.domain.ports.out;

import com.smartorders.domain.model.Cliente;
import java.util.List;
import java.util.Optional;

public interface ClienteRepositoryPort {
    Cliente guardar(Cliente cliente);
    Optional<Cliente> buscarPorId(String id);
    List<Cliente> listarTodos();
}
