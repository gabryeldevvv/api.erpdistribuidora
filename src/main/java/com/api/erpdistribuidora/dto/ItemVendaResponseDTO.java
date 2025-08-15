package com.api.erpdistribuidora.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ItemVendaResponseDTO {
    private Long id;
    private Long idProduto;
    private String nomeProduto;
    private Integer quantidade;
    private BigDecimal precoUnitario;
    private BigDecimal desconto;
}


