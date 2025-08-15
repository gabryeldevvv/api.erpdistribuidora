package com.api.erpdistribuidora.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class VendaResponseDTO {
    // comentario
    private Long id;
    private LocalDateTime dataVenda;
    private String status;
    private Boolean estoqueProcessado;
    private String observacoes;
    private List<ItemVendaResponseDTO> itens;
}

