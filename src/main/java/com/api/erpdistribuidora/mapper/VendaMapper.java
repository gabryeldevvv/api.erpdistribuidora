package com.api.erpdistribuidora.mapper;

import com.api.erpdistribuidora.dto.ItemVendaResponseDTO;
import com.api.erpdistribuidora.dto.VendaResponseDTO;
import com.api.erpdistribuidora.model.Venda;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VendaMapper {

    public VendaResponseDTO toResponseDTO(Venda venda) {
        if (venda == null) return null;
        List<ItemVendaResponseDTO> itens = venda.getItens() == null ? List.of()
                : venda.getItens().stream().map(item ->
                ItemVendaResponseDTO.builder()
                        .id(item.getId())
                        .idProduto(item.getProduto() != null ? item.getProduto().getId() : null)
                        .nomeProduto(item.getProduto() != null ? item.getProduto().getNome() : null)
                        .quantidade(item.getQuantidade())
                        .precoUnitario(item.getPrecoUnitario())
                        .desconto(item.getDesconto())
                        .build()
        ).toList();

        return VendaResponseDTO.builder()
                .id(venda.getId())
                .dataVenda(venda.getDataVenda())
                .status(venda.getStatus())
                .estoqueProcessado(venda.isEstoqueProcessado())
                .observacoes(venda.getObservacoes())
                .itens(itens)
                .build();
    }
}
