package com.api.erpdistribuidora.service;

import com.api.erpdistribuidora.dto.EstoqueResponseDTO;
import com.api.erpdistribuidora.dto.EstoqueRequestDTO;
import com.api.erpdistribuidora.dto.LocalResponseDTO;
import com.api.erpdistribuidora.exception.EstoqueNaoEncontradoException;
import com.api.erpdistribuidora.exception.LocalNaoEncontradoException;
import com.api.erpdistribuidora.mapper.EstoqueMapper;
import com.api.erpdistribuidora.mapper.LocalMapper;
import com.api.erpdistribuidora.model.Estoque;
import com.api.erpdistribuidora.model.Local;
import com.api.erpdistribuidora.model.Produto;
import com.api.erpdistribuidora.repository.EstoqueRepository;
import com.api.erpdistribuidora.repository.LocalRepository;
import com.api.erpdistribuidora.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import lombok.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EstoqueService {

    private final EstoqueRepository estoqueRepository;
    private final ProdutoRepository produtoRepository;
    private final LocalRepository localRepository;
    private final EstoqueMapper estoqueMapper;
    private final LocalMapper localMapper;

    @Transactional(readOnly = true)
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
        // valida produto e local
        Produto produto = produtoRepository.findById(dto.getIdProduto())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Produto inválido"));
        Local local = localRepository.findById(dto.getIdLocal())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Local inválido"));

        // impedir duplicidade
        estoqueRepository.findByProdutoIdAndLocalId(produto.getId(), local.getId())
                .ifPresent(e -> { throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe estoque para este produto neste local"); });

        Estoque novo = Estoque.builder()
                .produto(produto)
                .local(local)
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
        if (atualizacoes.getLocal() != null && atualizacoes.getLocal().getId() != null) {
            Local l = localRepository.findById(atualizacoes.getLocal().getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Local inválido"));
            existente.setLocal(l);
        }
        if (atualizacoes.getQuantidade() != null) {
            existente.setQuantidade(atualizacoes.getQuantidade());
        }
        try {
            return estoqueRepository.save(existente);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Duplicidade de estoque para produto/local", e);
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

    // ===== Agrupamento por Local =====
    @Transactional(readOnly = true)
    public List<EstoquePorLocalResponseDTO> listarAgrupadoPorLocal() {
        List<Estoque> todos = estoqueRepository.findAll();
        Map<Local, List<Estoque>> agrupado = todos.stream()
                .collect(Collectors.groupingBy(Estoque::getLocal, LinkedHashMap::new, Collectors.toList()));

        List<EstoquePorLocalResponseDTO> resposta = new ArrayList<>();
        for (Map.Entry<Local, List<Estoque>> entry : agrupado.entrySet()) {
            Local local = entry.getKey();
            List<EstoqueResponseDTO> itens = estoqueMapper.toResponseDTOList(entry.getValue());
            EstoquePorLocalResponseDTO dto = new EstoquePorLocalResponseDTO(
                    localMapper.toResponseDTO(local), itens
            );
            resposta.add(dto);
        }
        return resposta;
    }

    // DTO composto (apenas para resposta agregada)
    @Getter @Setter @AllArgsConstructor
    public static class EstoquePorLocalResponseDTO {
        private LocalResponseDTO local;
        private List<EstoqueResponseDTO> itens;
    }
}
