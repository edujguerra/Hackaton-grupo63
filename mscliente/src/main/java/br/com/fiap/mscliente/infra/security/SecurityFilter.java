package br.com.fiap.mscliente.infra.security;

import br.com.fiap.mscliente.infra.exception.UnauthorizedException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            var tokenJWT = recuperarToken(request);
            String url = request.getRequestURL().toString();

            if (tokenJWT != null) {
                var subject = tokenService.getSubject(tokenJWT);
                var authentication = new UsernamePasswordAuthenticationToken(subject, null, AUTHORITIES);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            if (tokenJWT == null &&
                    request.getRequestURL().toString().toLowerCase().contains("cliente"))
            {
                throw new UnauthorizedException("Token não pode ser vazio.");
            }
            filterChain.doFilter(request, response);

        } catch (Exception ex) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setCharacterEncoding("UTF-8");
            response.getWriter().println(ex.getLocalizedMessage());
        }
    }

    @SuppressWarnings("serial" )
    private static List<GrantedAuthority> AUTHORITIES = new ArrayList<GrantedAuthority>(1) {{
        add(new SimpleGrantedAuthority("ROLE_USER"));
    }};

    private String recuperarToken(HttpServletRequest request) {
        var authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null) {
            return authorizationHeader.replace("Bearer ", "");
        }

        return null;
    }
}
