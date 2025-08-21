package com.api.erpdistribuidora.dto;

import lombok.*;
import jakarta.validation.constraints.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class LocalRequestDTO {

    @NotBlank(message = "nome é obrigatório")
    @Size(max = 80, message = "nome deve ter no máximo 80 caracteres")
    private String nome;

    @Size(max = 255, message = "descricao deve ter no máximo 255 caracteres")
    private String descricao;
}
