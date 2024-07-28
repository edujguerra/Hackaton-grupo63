package br.com.fiap.mslogin.model;

import br.com.fiap.mslogin.entity.User;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public record SingUpRequest (

        @NotNull(message = "Usuário é obrigatório")
        @Size(min = 4, message = "Nome deve ter no mínimo 4 caracteres")
        String usuario,

        @NotNull(message = "Senha é obrigatório")
        @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
        String senha
) {
    public User toUser() {
        return User.builder()
                .usuario(usuario)
                .senha(senha)
                .build();
    }
}
