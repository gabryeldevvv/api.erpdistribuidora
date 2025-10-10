// src/main/java/com/api/erpdistribuidora/service/CategoriaService.java
package com.api.erpdistribuidora.service;

import com.api.erpdistribuidora.dto.CategoriaRequestDTO;
import com.api.erpdistribuidora.dto.CategoriaResponseDTO;
import com.api.erpdistribuidora.exception.CategoriaNaoEncontradaException;
import com.api.erpdistribuidora.exception.RegraCategoriaInvalidaException;
import com.api.erpdistribuidora.mapper.CategoriaMapper;
import com.api.erpdistribuidora.model.Categoria;
import com.api.erpdistribuidora.model.TipoCategoria;
import com.api.erpdistribuidora.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoriaService {

    private final CategoriaRepository repository;
    private final CategoriaMapper mapper;

    public CategoriaResponseDTO criar(CategoriaRequestDTO dto) {
        Categoria categoria = mapper.toEntity(dto);

        // id_publico único
        if (repository.existsByIdPublico(dto.getIdPublico())) {
            throw new RegraCategoriaInvalidaException("idPublico já existe: " + dto.getIdPublico());
        }

        // ligar (e validar) pai conforme regras
        Categoria pai = resolverEValidarPai(dto.getIdCategoriaPai(), dto.getTipo(), null);
        categoria.setCategoriaPai(pai);

        Categoria salvo = repository.save(categoria);
        return mapper.toResponseDTO(salvo);
    }

    public CategoriaResponseDTO atualizar(Long id, CategoriaRequestDTO dto) {
        Categoria entity = repository.findById(id)
                .orElseThrow(() -> new CategoriaNaoEncontradaException(id));

        // atualizar campos básicos
        if (dto.getIdPublico() != null && !dto.getIdPublico().equals(entity.getIdPublico())) {
            if (repository.existsByIdPublico(dto.getIdPublico())) {
                throw new RegraCategoriaInvalidaException("idPublico já existe: " + dto.getIdPublico());
            }
            entity.setIdPublico(dto.getIdPublico());
        }
        if (dto.getNome() != null) entity.setNome(dto.getNome());
        if (dto.getTipo() != null) entity.setTipo(dto.getTipo());

        // validar e setar pai
        Categoria pai = resolverEValidarPai(dto.getIdCategoriaPai(), entity.getTipo(), entity.getId());
        entity.setCategoriaPai(pai);

        Categoria salvo = repository.save(entity);
        return mapper.toResponseDTO(salvo);
    }

    @Transactional(readOnly = true)
    public CategoriaResponseDTO buscarPorId(Long id) {
        return repository.findById(id)
                .map(mapper::toResponseDTO)
                .orElseThrow(() -> new CategoriaNaoEncontradaException(id));
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> listarTodos() {
        return mapper.toResponseDTOList(repository.findAll());
    }

    public void deletar(Long id) {
        Categoria entity = repository.findById(id)
                .orElseThrow(() -> new CategoriaNaoEncontradaException(id));

        // impedir remoção se houver filhos
        if (repository.countByCategoriaPaiId(id) > 0) {
            throw new RegraCategoriaInvalidaException("Não é possível excluir: existem subcategorias vinculadas.");
        }
        repository.delete(entity);
    }

    /** Regras de negócio conforme DDL/trigger informados */
    private Categoria resolverEValidarPai(Long idPai, TipoCategoria tipoAtual, Long idAtual) {
        if (tipoAtual == TipoCategoria.Departamento) {
            // Departamento NÃO pode ter pai
            if (idPai != null) {
                throw new RegraCategoriaInvalidaException("Departamento não pode possuir categoria-pai.");
            }
            return null;
        }

        // tipoAtual == CATEGORIA
        if (idPai == null) {
            throw new RegraCategoriaInvalidaException("Categoria deve possuir um Departamento via categoria-pai.");
        }

        if (idAtual != null && idPai.equals(idAtual)) {
            // evita loop direto (self-parent)
            throw new RegraCategoriaInvalidaException("categoria-pai não pode ser a própria categoria.");
        }

        Categoria pai = repository.findById(idPai)
                .orElseThrow(() -> new RegraCategoriaInvalidaException("Categoria-pai inexistente."));

        if (pai.getTipo() != TipoCategoria.Departamento) {
            throw new RegraCategoriaInvalidaException("Categoria-pai deve ser do tipo 'Departamento'.");
        }

        return pai;
    }
}
