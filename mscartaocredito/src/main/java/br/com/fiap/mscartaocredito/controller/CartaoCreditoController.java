package br.com.fiap.mscartaocredito.controller;

import br.com.fiap.mscartaocredito.infra.exception.LimiteCartoesException;
import br.com.fiap.mscartaocredito.infra.exception.RegraNegocioException;
import br.com.fiap.mscartaocredito.model.CartaoCreditoDTO;
import br.com.fiap.mscartaocredito.service.CartaoCreditoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/cartao")
public class CartaoCreditoController {

    @Autowired
    CartaoCreditoService cartaoCreditoService;

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
        }
        //TODO: Verificar se cai nesse erro direto ou se precisa tratar
        catch (AuthorizationDeniedException e) {
            return new ResponseEntity<>("Erro de autorização.", HttpStatus.UNAUTHORIZED);
        }
    }

}
