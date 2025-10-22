// src/main/java/com/api/erpdistribuidora/mapper/UsuarioMapper.java
package com.api.erpdistribuidora.mapper;

import com.api.erpdistribuidora.dto.UsuarioRequestDTO;
import com.api.erpdistribuidora.dto.UsuarioResponseDTO;
import com.api.erpdistribuidora.model.Usuario;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UsuarioMapper {

    public Usuario toEntity(UsuarioRequestDTO dto) {
        if (dto == null) return null;
        return Usuario.builder()
                .nome(dto.getNome())
                .email(dto.getEmail())
                .senha(dto.getSenha())
                .build();
    }

    public UsuarioResponseDTO toResponseDTO(Usuario entity) {
        if (entity == null) return null;
        return UsuarioResponseDTO.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .email(entity.getEmail())
                .build();
    }

    public List<UsuarioResponseDTO> toResponseDTOList(List<Usuario> entities) {
        if (entities == null) return List.of();
        return entities.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Atualiza a entidade com os campos não nulos do DTO.
     * - Campos nulos no DTO são ignorados (não sobrescrevem).
     * - Para senha, além de null, string vazia também é ignorada,
     *   evitando apagar a senha quando o front envia "" para "manter atual".
     */
    public void updateEntityFromDto(UsuarioRequestDTO dto, Usuario entity) {
        if (dto == null || entity == null) return;

        if (dto.getNome() != null) {
            entity.setNome(dto.getNome());
        }
        if (dto.getEmail() != null) {
            entity.setEmail(dto.getEmail());
        }
        if (dto.getSenha() != null && !dto.getSenha().isBlank()) {
            entity.setSenha(dto.getSenha());
        }
    }
}
