package br.com.fiap.mspagamento.infra;

import br.com.fiap.mspagamento.infra.security.SecurityConfigurations;
import br.com.fiap.mspagamento.infra.security.SecurityFilter;
import br.com.fiap.mspagamento.infra.security.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

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
}
