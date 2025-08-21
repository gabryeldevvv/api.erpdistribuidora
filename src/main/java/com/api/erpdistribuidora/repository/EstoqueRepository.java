package com.api.erpdistribuidora.repository;

import com.api.erpdistribuidora.model.Estoque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstoqueRepository extends JpaRepository<Estoque, Long> {
    Optional<Estoque> findByProdutoIdAndLocalId(Long idProduto, Long idLocal);

    List<Estoque> findByProdutoId(Long idProduto);

    List<Estoque> findByProdutoIdOrderByQuantidadeDesc(Long idProduto);
}
