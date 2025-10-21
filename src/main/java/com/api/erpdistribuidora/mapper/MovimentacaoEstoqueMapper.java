package com.api.erpdistribuidora.mapper;

import com.api.erpdistribuidora.dto.MovimentacaoEstoqueRequestDTO;
import com.api.erpdistribuidora.dto.MovimentacaoEstoqueResponseDTO;
import com.api.erpdistribuidora.model.MovimentacaoEstoque;
import com.api.erpdistribuidora.model.Produto;
import com.api.erpdistribuidora.model.Usuario;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MovimentacaoEstoqueMapper {

    public MovimentacaoEstoque toEntity(MovimentacaoEstoqueRequestDTO dto) {
        if (dto == null) return null;
        Produto produto = new Produto();
        produto.setId(dto.getIdProduto());
        Usuario usuario = new Usuario();
        usuario.setId(dto.getIdUsuario());
        return MovimentacaoEstoque.builder()
                .produto(produto)
                .tipo(dto.getTipo())
                .quantidade(dto.getQuantidade())
                .referencia(dto.getReferencia())
                .usuario(usuario)
                .build();
    }

    public MovimentacaoEstoqueResponseDTO toResponseDTO(MovimentacaoEstoque entity) {
        if (entity == null) return null;
        return MovimentacaoEstoqueResponseDTO.builder()
                .id(entity.getId())
                .idProduto(entity.getProduto() != null ? entity.getProduto().getId() : null)
                .nomeProduto(entity.getProduto() != null ? entity.getProduto().getNome() : null)
                .tipo(entity.getTipo())
                .quantidade(entity.getQuantidade())
                .dataMovimentacao(entity.getDataMovimentacao())
                .referencia(entity.getReferencia())
                .idUsuario(entity.getUsuario().getId())
                .build();
    }

    public List<MovimentacaoEstoqueResponseDTO> toResponseDTOList(List<MovimentacaoEstoque> entities) {
        return entities.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }
}

