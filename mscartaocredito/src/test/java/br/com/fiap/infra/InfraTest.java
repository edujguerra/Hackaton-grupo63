package br.com.fiap.infra;

import br.com.fiap.infra.exception.AutorizacaoException;
import br.com.fiap.infra.exception.RegraNegocioException;
import br.com.fiap.infra.security.SecurityConfigurations;
import br.com.fiap.infra.security.SecurityFilter;
import br.com.fiap.infra.security.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

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
    public void test_security_filter_chain_configured_correctly() throws Exception {
        HttpSecurity http = mock(HttpSecurity.class);
        SecurityFilter securityFilter = mock(SecurityFilter.class);
        SecurityConfigurations securityConfigurations = new SecurityConfigurations();
        ReflectionTestUtils.setField(securityConfigurations, "securityFilter", securityFilter);

        when(http.csrf(any())).thenReturn(http);
        when(http.sessionManagement(any())).thenReturn(http);
        when(http.authorizeHttpRequests(any())).thenReturn(http);
        when(http.addFilterBefore(any(), eq(UsernamePasswordAuthenticationFilter.class))).thenReturn(http);

        SecurityFilterChain result = securityConfigurations.securityFilterChain(http);

        verify(http).csrf(any());
        verify(http).sessionManagement(any());
        verify(http).authorizeHttpRequests(any());
        verify(http).addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Test
    public void test_exception_with_valid_message() {
        String message = "Valid message";
        RegraNegocioException exception = new RegraNegocioException(message);
        assertEquals(message, exception.getMessage());
    }

    @Test
    public void testExceptionMessage() {
        String expectedMessage = "Mensagem de erro";

        AutorizacaoException exception = new AutorizacaoException(expectedMessage);

        assertEquals(expectedMessage, exception.getMessage(), "A mensagem da exceção não corresponde à esperada.");
    }

}
