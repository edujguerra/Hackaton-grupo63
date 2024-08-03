package br.com.fiap.mslogin.controller;

import br.com.fiap.mslogin.model.AuthenticateUser;
import br.com.fiap.mslogin.model.AuthenticationRequest;
import br.com.fiap.mslogin.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/autenticacao")
    public ResponseEntity<AuthenticateUser> login(@RequestBody @Valid AuthenticationRequest authenticationRequest) {

        return ResponseEntity.ok(authService.authenticate(authenticationRequest));
    }

    public AuthController(){
    }

}
