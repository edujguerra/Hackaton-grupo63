package br.com.fiap.mslogin.model;

import br.com.fiap.mslogin.entity.User;

public record UserDTO(
        Long id,
        String usuario
) {

    public static UserDTO fromUser(User user) {
        return new UserDTO(user.getId(), user.getUsuario());
    }
}
