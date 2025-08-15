package com.api.erpdistribuidora.service;

import com.api.erpdistribuidora.model.MovimentacaoEstoque;
import com.api.erpdistribuidora.repository.MovimentacaoEstoqueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MovimentacaoEstoqueService {

    private final MovimentacaoEstoqueRepository movimentacaoRepository;

    public List<MovimentacaoEstoque> listarPorProduto(Long idProduto) {
        return movimentacaoRepository.findByProdutoIdOrderByDataMovimentacaoDesc(idProduto);
    }
}
