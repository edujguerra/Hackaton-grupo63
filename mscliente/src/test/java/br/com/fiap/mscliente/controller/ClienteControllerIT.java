package br.com.fiap.mscliente.controller;

import br.com.fiap.mscliente.infra.exception.UnauthorizedException;
import br.com.fiap.mscliente.infra.security.TokenService;
import br.com.fiap.mscliente.model.Cliente;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.stereotype.Component;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Component
public class ClienteControllerIT {

    private TokenService tokenService = new TokenService();

    @Test
    void TestarToken() throws Exception {
        UnauthorizedException erro = new UnauthorizedException("Usuário e/ou senha inválido(s).");

        try {
            tokenService.getClains("123");
        } catch (UnauthorizedException ex) {

        }
    }

}