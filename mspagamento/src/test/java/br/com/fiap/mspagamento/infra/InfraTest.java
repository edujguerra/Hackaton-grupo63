package br.com.fiap.mspagamento.infra;

import br.com.fiap.mspagamento.infra.exception.LimiteException;
import br.com.fiap.mspagamento.infra.exception.PagamentoDuplicadoException;
import br.com.fiap.mspagamento.infra.exception.PagamentoException;
import br.com.fiap.mspagamento.infra.exception.TratadorDeErros;
import br.com.fiap.mspagamento.infra.security.SecurityConfigurations;
import br.com.fiap.mspagamento.infra.security.SecurityFilter;
import br.com.fiap.mspagamento.infra.security.TokenService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class InfraTest {

    @Test
    public void test_token_retrieved_from_request_header() throws ServletException, IOException {
        SecurityFilter securityFilter = new SecurityFilter();
        TokenService tokenService = mock(TokenService.class);
        ReflectionTestUtils.setField(securityFilter, "tokenService", tokenService);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(request.getHeader("Authorization")).thenReturn("Bearer validToken");
        when(tokenService.getSubject("validToken")).thenReturn("user");
        when(tokenService.getClains("validToken")).thenReturn("claims");

        securityFilter.doFilterInternal(request, response, filterChain);

        verify(request).getHeader("Authorization");
        verify(tokenService).getSubject("validToken");
        verify(tokenService).getClains("validToken");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void test_http_security_object_is_null() {
        SecurityConfigurations securityConfigurations = new SecurityConfigurations();

        assertThrows(NullPointerException.class, () -> {
            securityConfigurations.securityFilterChain(null);
        });
    }

    @Test
    public void test_returns_authentication_manager_with_valid_configuration() throws Exception {
        AuthenticationConfiguration configuration = mock(AuthenticationConfiguration.class);
        AuthenticationManager expectedManager = mock(AuthenticationManager.class);
        when(configuration.getAuthenticationManager()).thenReturn(expectedManager);

        SecurityConfigurations securityConfigurations = new SecurityConfigurations();
        AuthenticationManager actualManager = securityConfigurations.authenticationManager(configuration);

        assertNotNull(actualManager);
        assertEquals(expectedManager, actualManager);
    }

        @Test
    public void test_exception_with_valid_message() {
        String message = "Valid message";
        LimiteException exception = new LimiteException(message);
        assertEquals(message, exception.getMessage());
    }

        @Test
    public void test_default_constructor_sets_correct_message() {
        PagamentoException exception = new PagamentoException();
        assertEquals("Ocorreu um erro com o pagamento.", exception.getMessage());
    }

    @Test
    public void test_exception_with_valid_message_PAG() {
        String message = "Valid message";
        PagamentoException exception = new PagamentoException(message);
        assertEquals(message, exception.getMessage());
    }
    @Test
    public void test_exception_with_valid_message_and_cause() {
        String message = "Valid message";
        Throwable cause = new Throwable("Cause");
        PagamentoException exception = new PagamentoException(message, cause);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    public void test_default_constructor_sets_correct_message_pagd() {
        PagamentoDuplicadoException exception = new PagamentoDuplicadoException();
        assertEquals("Pagamento já realizado.", exception.getMessage());
    }

    @Test
    public void test_exception_with_valid_message_pagd() {
        String message = "Valid message";
        PagamentoDuplicadoException exception = new PagamentoDuplicadoException(message);
        assertEquals(message, exception.getMessage());
    }
    @Test
    public void test_exception_with_valid_message_and_cause_pagd() {
        String message = "Valid message";
        Throwable cause = new Throwable("Cause");
        PagamentoDuplicadoException exception = new PagamentoDuplicadoException(message, cause);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

        @Test
    public void handles_entity_not_found_exception() {
        TratadorDeErros handler = new TratadorDeErros();
        ResponseEntity response = handler.tratarErro404();
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

        @Test
    public void test_handles_method_argument_not_valid_exception() {
        TratadorDeErros tratador = new TratadorDeErros();
        MethodParameter parameter = Mockito.mock(MethodParameter.class);
        BindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "objectName");
        bindingResult.addError(new FieldError("objectName", "field", "defaultMessage"));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity response = tratador.tratarErro400(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(((List<?>) response.getBody()).isEmpty());
    }

    @Test
    public void test_valid_token_returns_correct_subject() {
        String secret = "66e48fcca777ed0975ff8a7f51198db678aea9661298bcd34adace1ecefa2cce";
        TokenService tokenService = new TokenService();
        String validToken = JWT.create()
                .withIssuer("API Hackaton")
                .withSubject("testSubject")
                .sign(Algorithm.HMAC256(Base64.getDecoder().decode(secret)));
        String subject = tokenService.getSubject(validToken);
        assertEquals("testSubject", subject);
    }
}
