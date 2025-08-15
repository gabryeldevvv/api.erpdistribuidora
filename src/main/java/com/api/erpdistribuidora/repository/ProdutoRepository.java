package com.api.erpdistribuidora.repository;

import com.api.erpdistribuidora.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> { }

