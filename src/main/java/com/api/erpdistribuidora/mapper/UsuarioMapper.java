package com.api.erpdistribuidora.mapper;

import com.api.erpdistribuidora.dto.UsuarioRequestDTO;
import com.api.erpdistribuidora.dto.UsuarioResponseDTO;
import com.api.erpdistribuidora.model.Usuario;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    Usuario toEntity(UsuarioRequestDTO dto);

    UsuarioResponseDTO toResponseDTO(Usuario entity);

    List<UsuarioResponseDTO> toResponseDTOList(List<Usuario> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UsuarioRequestDTO dto, @MappingTarget Usuario entity);
}
