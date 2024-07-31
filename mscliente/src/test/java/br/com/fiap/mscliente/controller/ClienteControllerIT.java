package br.com.fiap.mscliente.controller;

import br.com.fiap.mscliente.infra.exception.UnauthorizedException;
import br.com.fiap.mscliente.infra.security.TokenService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

public class ClienteControllerIT {

    @Mock
    private TokenService tokenService;

    @Test
    void TestarToken() throws Exception {
        UnauthorizedException erro = new UnauthorizedException(401, "Usuário e/ou senha inválido(s).");

    }

}