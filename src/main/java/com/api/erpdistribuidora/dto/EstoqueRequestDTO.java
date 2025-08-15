package com.api.erpdistribuidora.dto;

import lombok.*;
import jakarta.validation.constraints.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class EstoqueRequestDTO {

    @NotNull(message = "idProduto é obrigatório")
    private Long idProduto;

    @NotBlank(message = "localizacao é obrigatória")
    @Size(max = 50, message = "localizacao deve ter no máximo 50 caracteres")
    private String localizacao;

    @NotNull(message = "quantidade é obrigatória")
    @PositiveOrZero(message = "quantidade deve ser >= 0")
    private Integer quantidade;
}

