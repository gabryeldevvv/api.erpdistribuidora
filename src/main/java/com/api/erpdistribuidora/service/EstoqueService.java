package com.api.erpdistribuidora.service;

import com.api.erpdistribuidora.model.Estoque;
import com.api.erpdistribuidora.repository.EstoqueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EstoqueService {

    private final EstoqueRepository estoqueRepository;

    public List<Estoque> listarPorProduto(Long idProduto) {
        return estoqueRepository.findByProdutoId(idProduto);
    }
}
