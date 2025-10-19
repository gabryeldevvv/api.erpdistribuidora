// src/main/java/com/api/erpdistribuidora/service/ProdutoService.java
package com.api.erpdistribuidora.service;

import com.api.erpdistribuidora.dto.ProdutoRequestDTO;
import com.api.erpdistribuidora.dto.ProdutoResponseDTO;
import com.api.erpdistribuidora.exception.ProdutoNaoEncontradoException;
import com.api.erpdistribuidora.exception.RegraCategoriaInvalidaException;
import com.api.erpdistribuidora.mapper.ProdutoMapper;
import com.api.erpdistribuidora.model.Categoria;
import com.api.erpdistribuidora.model.TipoCategoria;
import com.api.erpdistribuidora.model.Produto;
import com.api.erpdistribuidora.repository.CategoriaRepository;
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
    private final CategoriaRepository categoriaRepository;

    public ProdutoResponseDTO criar(ProdutoRequestDTO dto) {
        Produto entity = produtoMapper.toEntity(dto);

        // categoria obrigatória e deve ser do tipo 'Categoria' (não 'Departamento')
        Categoria categoria = resolverCategoriaValida(dto.getIdCategoria());
        entity.setCategoria(categoria);

        Produto salvo = produtoRepository.save(entity);
        return produtoMapper.toResponseDTO(salvo);
    }

    public ProdutoResponseDTO atualizar(Long id, ProdutoRequestDTO dto) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ProdutoNaoEncontradoException(id));

        if (dto.getNome() != null) produto.setNome(dto.getNome());
        if (dto.getDescricao() != null) produto.setDescricao(dto.getDescricao());
        if (dto.getDataValidade() != null) produto.setDataValidade(dto.getDataValidade());
        if (dto.getAtivo() != null) produto.setAtivo(dto.getAtivo());

        // Se veio idCategoria no update, validar e aplicar
        if (dto.getIdCategoria() != null) {
            Categoria categoria = resolverCategoriaValida(dto.getIdCategoria());
            produto.setCategoria(categoria);
        }

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

    // === Regra de negócio: categoria válida para o produto ===
    private Categoria resolverCategoriaValida(Long idCategoria) {
        if (idCategoria == null) {
            throw new RegraCategoriaInvalidaException("Produto deve possuir uma categoria (idCategoria obrigatório).");
        }

        Categoria categoria = categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new RegraCategoriaInvalidaException("Categoria informada não existe."));

        if (categoria.getTipo() != TipoCategoria.Categoria) {
            throw new RegraCategoriaInvalidaException(
                    "Produtos só podem ser associados a categorias (tipo=Categoria), não a Departamentos.");
        }

        return categoria;
    }
}
