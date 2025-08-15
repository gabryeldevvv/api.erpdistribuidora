package com.api.erpdistribuidora.repository;

import com.api.erpdistribuidora.model.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VendaRepository extends JpaRepository<Venda, Long> {
    List<Venda> findByStatusOrderByDataVendaDesc(String status);
}

