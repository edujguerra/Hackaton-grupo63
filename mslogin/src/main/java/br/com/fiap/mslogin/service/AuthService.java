package br.com.fiap.mslogin.service;

import br.com.fiap.mslogin.entity.User;
import br.com.fiap.mslogin.exception.UnauthorizedException;
import br.com.fiap.mslogin.exception.UserException;
import br.com.fiap.mslogin.model.AuthenticateUser;
import br.com.fiap.mslogin.model.AuthenticationRequest;
import br.com.fiap.mslogin.model.SingUpRequest;
import br.com.fiap.mslogin.model.UserDTO;
import br.com.fiap.mslogin.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
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


    public UserDTO createUser(SingUpRequest singUpRequest) {
        userRepository.findFirstByUsuario(singUpRequest.usuario())
                .ifPresent(user -> {
                    throw new UserException(
                            HttpStatus.BAD_REQUEST.value(),
                            "Usuário   " + singUpRequest.usuario() + " já existe"
                    );
                });

        var user = singUpRequest.toUser();
        user.setSenha(bCryptPasswordEncoder.encode(singUpRequest.senha()));

        final User createdUser = userRepository.save(user);
        return UserDTO.fromUser(createdUser);
    }

    public AuthenticateUser authenticate(AuthenticationRequest authenticationRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authenticationRequest.usuario(),
                            authenticationRequest.senha()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new UnauthorizedException(403, "Usuário e/ou senha inválido(s).");
        }

        UserDetails userDetails = loadUserByUsername(authenticationRequest.usuario());
        Optional<User> userOptional = userRepository.findFirstByUsuario(authenticationRequest.usuario());

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String jwt = generateToken(
                    userDetails.getUsername(),
                    user.getId()
            );

            return new AuthenticateUser(user.getId(), user.getUsuario(), jwt);

        }

        throw new UnauthorizedException(401, "Não autorizado.");
    }

    public void validateToken(String token) {
        jwtService.validateToken(token);
    }

    public String generateToken(String email, Long id) {
        return jwtService.generateToken(email, id);
    }

    private UserDetails loadUserByUsername(String usuario) throws UsernameNotFoundException {
        User user = userRepository.findFirstByUsuario(usuario).orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        return new org.springframework.security.core.userdetails.User(user.getUsuario(), user.getSenha(), new ArrayList<>());
    }
}
