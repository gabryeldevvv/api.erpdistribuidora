// src/main/java/com/api/erpdistribuidora/dto/CategoriaRequestDTO.java
package com.api.erpdistribuidora.dto;

import com.api.erpdistribuidora.model.TipoCategoria;
import jakarta.validation.constraints.*;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CategoriaRequestDTO {


    @NotBlank(message = "nome é obrigatório")
    @Size(max = 100, message = "nome deve ter no máximo 100 caracteres")
    private String nome;

    @NotNull(message = "tipo é obrigatório")
    private TipoCategoria tipo; // DEPARTAMENTO | CATEGORIA

    // Obrigatório quando tipo=CATEGORIA (validado em regra de negócio)
    private Long idCategoriaPai;
}
