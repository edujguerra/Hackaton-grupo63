package br.com.fiap.mslogin.service;

import br.com.fiap.mslogin.entity.User;
import br.com.fiap.mslogin.exception.UnauthorizedException;
import br.com.fiap.mslogin.model.AuthenticateUser;
import br.com.fiap.mslogin.model.AuthenticationRequest;
import br.com.fiap.mslogin.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthService {

    private UserRepository userRepository;

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    public AuthenticateUser authenticate(AuthenticationRequest authenticationRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authenticationRequest.usuario(),
                            authenticationRequest.senha()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new UnauthorizedException(401, "Usuário e/ou senha inválido(s).");
        }

        UserDetails userDetails = loadUserByUsername(authenticationRequest.usuario());
        Optional<User> userOptional = userRepository.findFirstByUsuario(authenticationRequest.usuario());

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String jwt90 = generateToken(
                    userDetails.getUsername(),
                    user.getId(),
                    90
            );

            String jwt2 = generateToken(
                    userDetails.getUsername(),
                    user.getId(),
                    2
            );

            return new AuthenticateUser( jwt90,jwt2);

        }

        throw new UnauthorizedException(401, "Não autorizado.");
    }

    public String generateToken(String email, Long id, Integer minutos) {
        return jwtService.generateToken(email, id, minutos);
    }

    private UserDetails loadUserByUsername(String usuario) throws UsernameNotFoundException {
        User user = userRepository.findFirstByUsuario(usuario).orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        return new org.springframework.security.core.userdetails.User(user.getUsuario(), user.getSenha(), new ArrayList<>());
    }
}
