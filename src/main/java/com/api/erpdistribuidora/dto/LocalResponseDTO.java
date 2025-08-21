package com.api.erpdistribuidora.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class LocalResponseDTO {
    private Long id;
    private String nome;
    private String descricao;
}
