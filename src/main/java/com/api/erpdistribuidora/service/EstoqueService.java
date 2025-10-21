package com.api.erpdistribuidora.service;

import com.api.erpdistribuidora.dto.EstoqueResponseDTO;
import com.api.erpdistribuidora.dto.EstoqueRequestDTO;
import com.api.erpdistribuidora.exception.EstoqueNaoEncontradoException;
import com.api.erpdistribuidora.mapper.EstoqueMapper;
import com.api.erpdistribuidora.model.Estoque;
import com.api.erpdistribuidora.model.Produto;
import com.api.erpdistribuidora.repository.EstoqueRepository;
import com.api.erpdistribuidora.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
@RequiredArgsConstructor
public class EstoqueService {

    private final EstoqueRepository estoqueRepository;
    private final ProdutoRepository produtoRepository;
    private final EstoqueMapper estoqueMapper;

    @Transactional(readOnly = true )
    public List<EstoqueResponseDTO> listar() {
        return estoqueMapper.toResponseDTOList(estoqueRepository.findAll());
    }

    @Transactional(readOnly = true)
    public EstoqueResponseDTO buscarPorId(Long id) {
        return estoqueRepository.findById(id)
                .map(estoqueMapper::toResponseDTO)
                .orElseThrow(() -> new EstoqueNaoEncontradoException(id));
    }

    @Transactional
    public EstoqueResponseDTO criar(EstoqueRequestDTO dto) {
        // valida produto
        Produto produto = produtoRepository.findById(dto.getIdProduto())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Produto inválido"));


        Estoque novo = Estoque.builder()
                .produto(produto)
                .quantidade(dto.getQuantidade())
                .build();
        Estoque salvo = estoqueRepository.save(novo);
        return estoqueMapper.toResponseDTO(salvo);
    }

    @Transactional
    public Estoque atualizar(Long id, Estoque atualizacoes) {
        Estoque existente = estoqueRepository.findById(id)
                .orElseThrow(() -> new EstoqueNaoEncontradoException(id));

        if (atualizacoes.getProduto() != null && atualizacoes.getProduto().getId() != null) {
            Produto p = produtoRepository.findById(atualizacoes.getProduto().getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Produto inválido"));
            existente.setProduto(p);
        }
        if (atualizacoes.getQuantidade() != null) {
            existente.setQuantidade(atualizacoes.getQuantidade());
        }
        try {
            return estoqueRepository.save(existente);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Duplicidade de estoque para produto", e);
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
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Não foi possível remover: o estoque está sendo referenciado.",
                    e
            );
        }
    }

}
