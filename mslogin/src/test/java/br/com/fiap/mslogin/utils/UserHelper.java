package br.com.fiap.mslogin.utils;

import br.com.fiap.mslogin.entity.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.Column;

public abstract class UserHelper {
    public static User gerarUser() {
        User user = new User();
        user.setUsuario("Eduardo");
        user.setSenha("10212");

        return user;
    }

    public static String asJsonString(final Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
    }
}
