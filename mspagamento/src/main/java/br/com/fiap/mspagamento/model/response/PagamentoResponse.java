package br.com.fiap.mspagamento.model.response;


import br.com.fiap.mspagamento.model.enums.MetodoPagamento;
import br.com.fiap.mspagamento.model.enums.StatusPagamento;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PagamentoResponse {
    
    private Double valor;

    private String descricao;

    private MetodoPagamento metodoPagamento;

    private StatusPagamento statusPagamento;
    
}
