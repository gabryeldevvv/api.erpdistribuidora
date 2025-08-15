package com.api.erpdistribuidora.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class VendaRequestDTO {

    @Pattern(regexp = "concluída|pendente|rascunho|cancelada",
            message = "status deve ser concluída, pendente, rascunho ou cancelada")
    private String status;

    @Size(max = 10000, message = "observacoes excede o tamanho permitido")
    private String observacoes;

    // pode ser vazio para criar a venda primeiro e adicionar itens depois
    private List<ItemVendaRequestDTO> itens;
}
