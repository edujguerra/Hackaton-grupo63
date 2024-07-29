package br.com.fiap.mscartaocredito.service;

import br.com.fiap.mscartaocredito.model.CartaoCredito;
import br.com.fiap.mscartaocredito.model.CartaoCreditoDTO;

public interface CartaoCreditoService {

    CartaoCredito gerarCartaoCredito(CartaoCreditoDTO cartaoCreditoDTO);
}
