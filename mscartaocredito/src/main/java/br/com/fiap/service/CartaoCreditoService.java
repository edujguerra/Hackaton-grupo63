package br.com.fiap.service;


import br.com.fiap.model.CartaoCredito;
import br.com.fiap.model.CartaoCreditoDTO;

public interface CartaoCreditoService {

    CartaoCredito gerarCartaoCredito(CartaoCreditoDTO cartaoCreditoDTO);
}
