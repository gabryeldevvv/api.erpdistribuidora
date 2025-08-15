package com.api.erpdistribuidora.repository;

import com.api.erpdistribuidora.model.Estoque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstoqueRepository extends JpaRepository<Estoque, Long> {
    Optional<Estoque> findByProdutoIdAndLocalizacao(Long idProduto, String localizacao);

    List<Estoque> findByProdutoId(Long idProduto);

    List<Estoque> findByProdutoIdOrderByQuantidadeDesc(Long idProduto);
}
