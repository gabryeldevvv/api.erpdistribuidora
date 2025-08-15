package com.api.erpdistribuidora.service;

import com.api.erpdistribuidora.dto.ProdutoRequestDTO;
import com.api.erpdistribuidora.dto.ProdutoResponseDTO;
import com.api.erpdistribuidora.exception.ProdutoNaoEncontradoException;
import com.api.erpdistribuidora.mapper.ProdutoMapper;
import com.api.erpdistribuidora.model.Produto;
import com.api.erpdistribuidora.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final ProdutoMapper produtoMapper;

    public ProdutoResponseDTO criar(ProdutoRequestDTO dto) {
        Produto salvo = produtoRepository.save(produtoMapper.toEntity(dto));
        return produtoMapper.toResponseDTO(salvo);
    }

    public ProdutoResponseDTO atualizar(Long id, ProdutoRequestDTO dto) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ProdutoNaoEncontradoException(id));

        if (dto.getNome() != null) produto.setNome(dto.getNome());
        if (dto.getDescricao() != null) produto.setDescricao(dto.getDescricao());
        if (dto.getPrecoUnitario() != null) produto.setPrecoUnitario(dto.getPrecoUnitario());
        if (dto.getUnidadeMedida() != null) produto.setUnidadeMedida(dto.getUnidadeMedida());
        if (dto.getDataValidade() != null) produto.setDataValidade(dto.getDataValidade());
        if (dto.getAtivo() != null) produto.setAtivo(dto.getAtivo());

        Produto salvo = produtoRepository.save(produto);
        return produtoMapper.toResponseDTO(salvo);
    }

    @Transactional(readOnly = true)
    public ProdutoResponseDTO buscarPorId(Long id) {
        return produtoRepository.findById(id)
                .map(produtoMapper::toResponseDTO)
                .orElseThrow(() -> new ProdutoNaoEncontradoException(id));
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> listarTodos() {
        return produtoMapper.toResponseDTOList(produtoRepository.findAll());
    }

    public void deletar(Long id) {
        if (!produtoRepository.existsById(id)) throw new ProdutoNaoEncontradoException(id);
        produtoRepository.deleteById(id);
    }
}
