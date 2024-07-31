package br.com.fiap.mslogin.controller;

import br.com.fiap.mslogin.entity.User;
import br.com.fiap.mslogin.exception.UnauthorizedException;
import br.com.fiap.mslogin.model.AuthenticateUser;
import br.com.fiap.mslogin.model.AuthenticationRequest;
import br.com.fiap.mslogin.service.AuthService;
import br.com.fiap.mslogin.service.UsuarioService;
import br.com.fiap.mslogin.utils.UserHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.token.TokenService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UsuarioService userService;

    AutoCloseable mock;

    @BeforeEach
    void setup() {
        mock = MockitoAnnotations.openMocks(this);
        AuthController clienteController = new AuthController();

        mockMvc = MockMvcBuilders.standaloneSetup(clienteController)
                .build();

    }

    @AfterEach
    void tearDown() throws Exception {
        mock.close();
    }

    @MockBean
    private TokenService tokenService;

    @Test
    public void test_valid_credentials_return_authenticate_user_with_tokens() {
        AuthenticationRequest request = new AuthenticationRequest("validUser", "validPassword");
        AuthenticateUser expectedResponse = new AuthenticateUser("token90", "token2min");

        AuthService authService = mock(AuthService.class);
        when(authService.authenticate(request)).thenReturn(expectedResponse);

        AuthController authController = new AuthController(authService);
        ResponseEntity<AuthenticateUser> response = authController.login(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    public void test_invalid_credentials_throw_unauthorized_exception() {
        AuthenticationRequest request = new AuthenticationRequest("invalidUser", "invalidPassword");

        AuthService authService = mock(AuthService.class);
        when(authService.authenticate(request)).thenThrow(new UnauthorizedException(401, "Usuￃﾡrio e/ou senha invￃﾡlido(s)."));

        AuthController authController = new AuthController(authService);

        assertThrows(UnauthorizedException.class, () -> {
            authController.login(request);
        });
    }
}
