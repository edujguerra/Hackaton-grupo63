package br.com.fiap.mscliente.infra;

import br.com.fiap.mscliente.infra.security.SecurityFilter;
import br.com.fiap.mscliente.infra.security.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

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
}
