package br.com.fiap.mslogin.model;

public record AuthenticateUser(

        String token,

        String token2min
) {
}
