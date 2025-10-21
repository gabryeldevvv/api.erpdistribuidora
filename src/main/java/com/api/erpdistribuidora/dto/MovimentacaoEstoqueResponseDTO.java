package com.api.erpdistribuidora.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class MovimentacaoEstoqueResponseDTO {
    private Long id;
    private Long idProduto;
    private String nomeProduto;
    private String tipo;
    private Integer quantidade;
    private LocalDateTime dataMovimentacao;
    private String referencia;
    private Long idUsuario;
}
