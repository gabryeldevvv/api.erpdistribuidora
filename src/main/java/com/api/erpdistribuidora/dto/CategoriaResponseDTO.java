// src/main/java/com/api/erpdistribuidora/dto/CategoriaResponseDTO.java
package com.api.erpdistribuidora.dto;

import com.api.erpdistribuidora.model.TipoCategoria;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CategoriaResponseDTO {
    private Long id;
    private String idPublico;
    private String nome;
    private TipoCategoria tipo;
    private Long idCategoriaPai;
}
