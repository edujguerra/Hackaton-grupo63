package br.com.fiap.controller;

import br.com.fiap.infra.exception.LimiteCartoesException;
import br.com.fiap.infra.exception.RegraNegocioException;
import br.com.fiap.model.CartaoCredito;
import br.com.fiap.model.CartaoCreditoDTO;
import br.com.fiap.service.CartaoCreditoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/cartao")
public class CartaoCreditoController {

    CartaoCreditoService cartaoCreditoService;

    public CartaoCreditoController() {
    }

    public CartaoCreditoController(CartaoCreditoService cartaoCreditoService) {
        this.cartaoCreditoService = cartaoCreditoService;
    }

    @PostMapping
    public ResponseEntity<?> gerarCartaoCredito(@RequestBody CartaoCreditoDTO cartaoCreditoDTO) {
        try {
            return new ResponseEntity<>(cartaoCreditoService.gerarCartaoCredito(cartaoCreditoDTO), HttpStatus.OK);

        } catch (NoSuchElementException | RegraNegocioException e) {
            return new ResponseEntity<>("Cartão de Credito não gerado: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (LimiteCartoesException e) {
            return new ResponseEntity<>("Limite de cartoes atingido: " + e.getMessage(),
                    HttpStatus.FORBIDDEN);
        } catch (AuthorizationDeniedException e) {
            return new ResponseEntity<>("Erro de autorização.", HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<?> obterCartoesPorCpf(@PathVariable String cpf) {
        try {
            List<CartaoCredito> cartoesCredito = cartaoCreditoService.obterCartoesPorCpf(cpf);
            return new ResponseEntity<>(cartoesCredito, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum Cartao de credito encontrado para o CPF fornecido.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocorreu um erro ao processar a solicitação.");
        }
    }
}
