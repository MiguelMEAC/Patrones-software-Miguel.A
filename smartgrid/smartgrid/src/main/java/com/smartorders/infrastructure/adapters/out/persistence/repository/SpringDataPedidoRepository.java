package com.smartorders.infrastructure.adapters.out.persistence.repository;

import com.smartorders.infrastructure.adapters.out.persistence.entity.PedidoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataPedidoRepository extends JpaRepository<PedidoEntity, String> {
    Optional<PedidoEntity> findByCodigo(String codigo);
    List<PedidoEntity> findByClienteId(String clienteId);
}
