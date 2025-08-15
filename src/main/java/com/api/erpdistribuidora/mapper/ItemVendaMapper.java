package com.api.erpdistribuidora.mapper;

import com.api.erpdistribuidora.dto.ItemVendaRequestDTO;
import com.api.erpdistribuidora.dto.ItemVendaResponseDTO;
import com.api.erpdistribuidora.model.ItemVenda;
import com.api.erpdistribuidora.model.Produto;
import com.api.erpdistribuidora.model.Venda;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemVendaMapper {

    public ItemVenda toEntity(ItemVendaRequestDTO dto, Produto produto, Venda venda) {
        if (dto == null) return null;
        return ItemVenda.builder()
                .venda(venda)
                .produto(produto)
                .quantidade(dto.getQuantidade())
                .precoUnitario(dto.getPrecoUnitario())
                .desconto(dto.getDesconto())
                .build();
    }

    public ItemVendaResponseDTO toResponseDTO(ItemVenda entity) {
        if (entity == null) return null;
        return ItemVendaResponseDTO.builder()
                .id(entity.getId())
                .idProduto(entity.getProduto() != null ? entity.getProduto().getId() : null)
                .nomeProduto(entity.getProduto() != null ? entity.getProduto().getNome() : null)
                .quantidade(entity.getQuantidade())
                .precoUnitario(entity.getPrecoUnitario())
                .desconto(entity.getDesconto())
                .build();
    }

    public List<ItemVendaResponseDTO> toResponseDTOList(List<ItemVenda> entities) {
        return entities.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }
}
