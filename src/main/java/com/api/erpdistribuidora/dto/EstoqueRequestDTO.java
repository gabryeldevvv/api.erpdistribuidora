package com.api.erpdistribuidora.dto;

import lombok.*;
import jakarta.validation.constraints.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class EstoqueRequestDTO {

    @NotNull(message = "idProduto é obrigatório")
    private Long idProduto;

    @NotNull(message = "quantidade é obrigatória")
    @PositiveOrZero(message = "quantidade deve ser >= 0")
    private Integer quantidade;

    @NotNull(message = "idLocal é obrigatório")
    private Long idLocal;
}
