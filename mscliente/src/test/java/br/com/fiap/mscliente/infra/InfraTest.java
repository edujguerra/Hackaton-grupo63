package br.com.fiap.mscliente.infra;

import br.com.fiap.mscliente.infra.exception.UnauthorizedException;
import br.com.fiap.mscliente.infra.security.SecurityConfigurations;
import br.com.fiap.mscliente.infra.security.SecurityFilter;
import br.com.fiap.mscliente.infra.security.TokenService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
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
import java.util.Base64;

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

    @Test
    public void test_constructor_initializes_message_correctly() {
        String expectedMessage = "Unauthorized access";
        UnauthorizedException exception = new UnauthorizedException(expectedMessage);
        assertEquals(expectedMessage, exception.getMessage());
    }
}
