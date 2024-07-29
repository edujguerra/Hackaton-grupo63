package br.com.fiap.mscliente.controller;

import br.com.fiap.mscliente.model.Cliente;
import br.com.fiap.mscliente.service.ClienteService;
import br.com.fiap.mslogin.exception.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cliente")
public class ClienteController {

    private final ClienteService service;

    public ClienteController(ClienteService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Object> salvar(@RequestBody Cliente cliente) {

        try {
            return service.salvar(cliente);
        } catch (BadCredentialsException e) {
            throw new UnauthorizedException(401, "Usuário e/ou senha inválido(s).");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> buscarUm(@PathVariable Integer id) {

        return service.buscarUm(id);
    }

}
