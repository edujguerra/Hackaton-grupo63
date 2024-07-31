package br.com.fiap.service;


import br.com.fiap.model.CartaoCredito;
import br.com.fiap.model.CartaoCreditoDTO;

import java.util.List;

public interface CartaoCreditoService {

    CartaoCredito gerarCartaoCredito(CartaoCreditoDTO cartaoCreditoDTO);

    List<CartaoCredito> obterCartoesPorCpf(String cpf);
}
