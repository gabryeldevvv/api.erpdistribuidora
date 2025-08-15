package com.api.erpdistribuidora.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ProdutoResponseDTO {
    private Long id;
    private String nome;
    private String descricao;
    private BigDecimal precoUnitario;
    private String unidadeMedida;
    private LocalDate dataValidade;
    private LocalDateTime dataCadastro;
    private Boolean ativo;
}

