package com.api.erpdistribuidora.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ItemVendaRequestDTO {

    @NotNull(message = "idProduto é obrigatório")
    private Long idProduto;

    @NotNull(message = "quantidade é obrigatória")
    @Positive(message = "quantidade deve ser > 0")
    private Integer quantidade;

    @NotNull(message = "precoUnitario é obrigatório")
    @DecimalMin(value = "0.01", message = "precoUnitario deve ser > 0")
    private BigDecimal precoUnitario;

    @DecimalMin(value = "0.00", message = "desconto deve ser >= 0")
    private BigDecimal desconto;
}
