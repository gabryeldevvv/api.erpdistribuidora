// src/main/java/com/api/erpdistribuidora/dto/ProdutoRequestDTO.java
package com.api.erpdistribuidora.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ProdutoRequestDTO {

    @NotBlank(message = "nome é obrigatório")
    @Size(max = 100, message = "nome deve ter no máximo 100 caracteres")
    private String nome;

    private String descricao;

    @DecimalMin(value = "0.01", message = "precoUnitario deve ser maior que zero")
    private BigDecimal precoUnitario;

    @Size(max = 10, message = "unidadeMedida deve ter no máximo 10 caracteres")
    private String unidadeMedida;

    private LocalDate dataValidade;

    // opcional; default true na entidade
    private Boolean ativo;

    // >>> ADIÇÃO: id da categoria a ser vinculada ao produto
    // (validado na Service para garantir que não seja Departamento)
    @JsonProperty("idCategoria")
    private Long idCategoria;
}
