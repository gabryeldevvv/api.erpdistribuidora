package com.api.erpdistribuidora.mapper;

import com.api.erpdistribuidora.dto.EstoqueRequestDTO;
import com.api.erpdistribuidora.dto.EstoqueResponseDTO;
import com.api.erpdistribuidora.model.Estoque;
import com.api.erpdistribuidora.model.Produto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EstoqueMapper {

    public Estoque toEntity(EstoqueRequestDTO dto) {
        if (dto == null) return null;
        var builder = Estoque.builder()
                .localizacao(dto.getLocalizacao())
                .quantidade(dto.getQuantidade());

        if (dto.getIdProduto() != null) {
            Produto produto = new Produto();
            produto.setId(dto.getIdProduto());
            builder.produto(produto);
        }
        return builder.build();
    }

    public EstoqueResponseDTO toResponseDTO(Estoque entity) {
        if (entity == null) return null;
        return EstoqueResponseDTO.builder()
                .id(entity.getId())
                .idProduto(entity.getProduto() != null ? entity.getProduto().getId() : null)
                .nomeProduto(entity.getProduto() != null ? entity.getProduto().getNome() : null)
                .quantidade(entity.getQuantidade())
                .localizacao(entity.getLocalizacao())
                .ultimaAtualizacao(entity.getUltimaAtualizacao())
                .build();
    }

    public List<EstoqueResponseDTO> toResponseDTOList(List<Estoque> entities) {
        return entities.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }


}
