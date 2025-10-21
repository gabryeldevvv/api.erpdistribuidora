package com.api.erpdistribuidora.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UsuarioRequestDTO {

    @NotBlank(message = "nome é obrigatório")
    @Size(max = 100, message = "nome deve ter no máximo 100 caracteres")
    private String nome;

    @NotBlank(message = "email é obrigatório")
    @Email(message = "email inválido")
    @Size(max = 120, message = "email deve ter no máximo 120 caracteres")
    private String email;

    @NotBlank(message = "senha é obrigatório")
    @Size(max = 120, message = "senha deve ter no máximo 120 caracteres")
    private String senha;
}
