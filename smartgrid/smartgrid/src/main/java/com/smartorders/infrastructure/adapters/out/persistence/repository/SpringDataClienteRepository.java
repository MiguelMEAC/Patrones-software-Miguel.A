package com.smartorders.infrastructure.adapters.out.persistence.repository;

import com.smartorders.infrastructure.adapters.out.persistence.entity.ClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataClienteRepository extends JpaRepository<ClienteEntity, String> {
}
