package com.api.erpdistribuidora.mapper;

import com.api.erpdistribuidora.dto.ProdutoRequestDTO;
import com.api.erpdistribuidora.dto.ProdutoResponseDTO;
import com.api.erpdistribuidora.model.Produto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProdutoMapper {

    public Produto toEntity(ProdutoRequestDTO dto) {
        if (dto == null) return null;
        return Produto.builder()
                .nome(dto.getNome())
                .descricao(dto.getDescricao())
                .precoUnitario(dto.getPrecoUnitario())
                .unidadeMedida(dto.getUnidadeMedida())
                .dataValidade(dto.getDataValidade())
                .ativo(dto.getAtivo() != null ? dto.getAtivo() : true)
                .build();
    }

    public ProdutoResponseDTO toResponseDTO(Produto entity) {
        if (entity == null) return null;
        return ProdutoResponseDTO.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .descricao(entity.getDescricao())
                .precoUnitario(entity.getPrecoUnitario())
                .unidadeMedida(entity.getUnidadeMedida())
                .dataValidade(entity.getDataValidade())
                .dataCadastro(entity.getDataCadastro())
                .ativo(entity.isAtivo())
                .build();
    }

    public List<ProdutoResponseDTO> toResponseDTOList(List<Produto> entities) {
        return entities.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }
}
