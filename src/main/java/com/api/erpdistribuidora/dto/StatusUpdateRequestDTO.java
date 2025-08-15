package com.api.erpdistribuidora.dto;

import lombok.*;
import jakarta.validation.constraints.Pattern;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class StatusUpdateRequestDTO {
    @Pattern(regexp = "concluída|pendente|rascunho|cancelada",
            message = "status deve ser concluída, pendente, rascunho ou cancelada")
    private String status;
}
