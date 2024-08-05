package br.com.fiap.mspagamento.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RegistrarPagamentoResponse {

    private String cpf;

    private String numero;

    private Date data_validade;

    private String cvv;

    private Double valor;
}
