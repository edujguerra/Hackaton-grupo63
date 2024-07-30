package br.com.fiap.mspagamento.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PagamentoDTO {

    private Long id;

    private String cpf;

    private String numero;

    private Date data_validade;

    private String cvv;

    private Double valor;
}