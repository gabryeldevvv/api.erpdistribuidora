package com.api.erpdistribuidora.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UsuarioResponseDTO {
    private Long id;
    private String nome;
    private String email;
}
