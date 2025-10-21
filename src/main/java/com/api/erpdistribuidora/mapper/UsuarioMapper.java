package com.api.erpdistribuidora.mapper;

import com.api.erpdistribuidora.dto.UsuarioRequestDTO;
import com.api.erpdistribuidora.dto.UsuarioResponseDTO;
import com.api.erpdistribuidora.model.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    // Mapeia UsuarioRequestDTO para Usuario (para criação)
    Usuario toEntity(UsuarioRequestDTO dto);

    UsuarioResponseDTO toResponseDTO(Usuario entity);

    List<UsuarioResponseDTO> toResponseDTOList(List<Usuario> entities);

    // Atualiza uma entidade Usuario existente a partir de um UsuarioRequestDTO
    void updateEntityFromDto(UsuarioRequestDTO dto, @MappingTarget Usuario entity);
}
