// src/main/java/com/api/erpdistribuidora/mapper/CategoriaMapper.java
package com.api.erpdistribuidora.mapper;

import com.api.erpdistribuidora.dto.CategoriaRequestDTO;
import com.api.erpdistribuidora.dto.CategoriaResponseDTO;
import com.api.erpdistribuidora.model.Categoria;
import com.api.erpdistribuidora.model.TipoCategoria;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CategoriaMapper {

    public Categoria toEntity(CategoriaRequestDTO dto) {
        if (dto == null) return null;
        return Categoria.builder()
                .idPublico(dto.getIdPublico())
                .nome(dto.getNome())
                .tipo(dto.getTipo())
                // categoriaPai será ligado no service (para garantir existência e tipo)
                .build();
    }

    public CategoriaResponseDTO toResponseDTO(Categoria entity) {
        if (entity == null) return null;
        return CategoriaResponseDTO.builder()
                .id(entity.getId())
                .idPublico(entity.getIdPublico())
                .nome(entity.getNome())
                .tipo(entity.getTipo())
                .idCategoriaPai(entity.getCategoriaPai() != null ? entity.getCategoriaPai().getId() : null)
                .dataCadastro(entity.getDataCadastro())
                .build();
    }

    public List<CategoriaResponseDTO> toResponseDTOList(List<Categoria> entities) {
        return entities.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }
}
