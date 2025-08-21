package com.api.erpdistribuidora.mapper;

import com.api.erpdistribuidora.dto.LocalRequestDTO;
import com.api.erpdistribuidora.dto.LocalResponseDTO;
import com.api.erpdistribuidora.model.Local;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class LocalMapper {

    public Local toEntity(LocalRequestDTO dto) {
        if (dto == null) return null;
        return Local.builder()
                .nome(dto.getNome())
                .descricao(dto.getDescricao())
                .build();
    }

    public void updateEntity(Local entity, LocalRequestDTO dto) {
        if (dto.getNome() != null) entity.setNome(dto.getNome());
        if (dto.getDescricao() != null) entity.setDescricao(dto.getDescricao());
    }

    public LocalResponseDTO toResponseDTO(Local local) {
        if (local == null) return null;
        return LocalResponseDTO.builder()
                .id(local.getId())
                .nome(local.getNome())
                .descricao(local.getDescricao())
                .build();
    }

    public List<LocalResponseDTO> toResponseDTOList(List<Local> list) {
        return list.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }
}
