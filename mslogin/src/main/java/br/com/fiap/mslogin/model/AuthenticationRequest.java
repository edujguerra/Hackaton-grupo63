package br.com.fiap.mslogin.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AuthenticationRequest(

        @NotNull(message = "Usuário não pode ser vazio.")
        @Size(min = 4, message = "Mínimo de 4 characters.")
        String usuario,

        @NotNull(message = "Senha não pode ser vazia.")
        @Size(min = 6, message = "Mínimo de 6 characters.")
        String senha
) {
}
