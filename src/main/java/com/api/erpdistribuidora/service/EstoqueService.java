// src/main/java/com/api/erpdistribuidora/service/EstoqueService.java
package com.api.erpdistribuidora.service;

import com.api.erpdistribuidora.exception.EstoqueNaoEncontradoException;
import com.api.erpdistribuidora.model.Estoque;
import com.api.erpdistribuidora.repository.EstoqueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EstoqueService {

    private final EstoqueRepository estoqueRepository;

    public List<Estoque> listarTodos() {
        return estoqueRepository.findAll();
    }

    public List<Estoque> listarPorProduto(Long idProduto) {
        return estoqueRepository.findByProdutoId(idProduto);
    }

    public Estoque buscarPorId(Long id) {
        return estoqueRepository.findById(id)
                .orElseThrow(() -> new EstoqueNaoEncontradoException(id));
    }

    @Transactional
    public Estoque criar(Estoque entity) {
        try {
            return estoqueRepository.save(entity);
        } catch (DataIntegrityViolationException e) {
            // viola a unique (id_produto, localizacao)
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Já existe um estoque para este produto nesta localização.",
                    e
            );
        }
    }

    @Transactional
    public Estoque atualizar(Long id, Estoque dados) {
        Estoque existente = buscarPorId(id);

        // atualiza campos permitidos
        existente.setQuantidade(dados.getQuantidade());
        existente.setLocalizacao(dados.getLocalizacao());
        if (dados.getProduto() != null && dados.getProduto().getId() != null) {
            existente.setProduto(dados.getProduto());
        }

        try {
            // gerenciado pelo JPA; flush no commit
            return existente;
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Já existe um estoque para este produto nesta localização.",
                    e
            );
        }
    }

    @Transactional
    public void remover(Long id) {
        if (!estoqueRepository.existsById(id)) {
            throw new EstoqueNaoEncontradoException(id);
        }
        try {
            estoqueRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            // caso haja FK referenciando o estoque
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Não foi possível remover: o estoque está sendo referenciado.",
                    e
            );
        }
    }
}
