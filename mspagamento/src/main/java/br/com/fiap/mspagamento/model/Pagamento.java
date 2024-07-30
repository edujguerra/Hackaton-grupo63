package br.com.fiap.mspagamento.model;

import br.com.fiap.mspagamento.model.Enum.TipoPagamentoEnum;
import br.com.fiap.mspagamento.model.dto.ItemCarrinhoDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer idCarrinhoDeCompras;

    private int quantidadeTotal;

    private double valorTotal;

    private TipoPagamentoEnum tipoPagamento;

    private String statusPagamento;

}
