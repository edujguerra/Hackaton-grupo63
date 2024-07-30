package br.com.fiap.mscartaocredito.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CartaoCreditoDTO {

    private Long id;

    private String cpf;

    private Double limite;

    private String numero;

    private Date data_validade;

    private String cvv;
}
