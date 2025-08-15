package com.api.erpdistribuidora.dto;

import lombok.*;
import jakarta.validation.constraints.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class MovimentacaoEstoqueRequestDTO {

    @NotNull(message = "idProduto é obrigatório")
    private Long idProduto;

    @NotBlank(message = "tipo é obrigatório")
    @Pattern(regexp = "entrada|saida|ajuste",
            message = "tipo deve ser 'entrada', 'saida' ou 'ajuste'")
    private String tipo;

    @NotNull(message = "quantidade é obrigatória")
    @Positive(message = "quantidade deve ser > 0")
    private Integer quantidade;

    @Size(max = 100, message = "referencia deve ter no máximo 100 caracteres")
    private String referencia;

    private Integer idUsuario; // opcional; se existir autenticação, vem do contexto
}

