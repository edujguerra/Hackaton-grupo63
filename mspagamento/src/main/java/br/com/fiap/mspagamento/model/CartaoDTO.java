package br.com.fiap.mspagamento.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartaoDTO {

    private Long id;

    private String cpf;

    private Double limite;

    private String numero;

    private Date data_validade;

    private String cvv;

}

