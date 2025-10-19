package com.api.erpdistribuidora.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class EstoqueResponseDTO {
    private Long id;
    private Long idProduto;
    private String nomeProduto;
    private Integer quantidade;
    private LocalDateTime ultimaAtualizacao;
}
