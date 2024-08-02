package br.com.fiap.mspagamento.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;

import java.util.Date;

import br.com.fiap.mspagamento.model.enums.MetodoPagamento;
import br.com.fiap.mspagamento.model.enums.StatusPagamento;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotBlank(message = "CPF não pode ser vazio.")
    @Column(name = "cpf", nullable = false)
    private String cpf;

    @NotBlank(message = "Numero do cartão não pode ser vazio.")
    @Column(name = "numero", nullable = false)
    private String numero;

    @NotBlank(message = "Data de validade do cartão não pode ser vazio.")
    @Column(name = "data_validade", nullable = false)
    private Date data_validade;

    @NotBlank(message = "CVV do cartão não pode ser vazio.")
    @Column(name = "cvv", nullable = false)
    private String cvv;

    @NotBlank(message = "Valor do pagamento não pode ser vazio.")
    @Column(name = "valor", nullable = false)
    private Double valor;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusPagamento statusPagamento;

    @Enumerated(EnumType.STRING)
    @Column(name = "forma_pagamento", nullable = false)
    private MetodoPagamento metodoPagamento;
    
    @NotBlank(message = "Descricao do pagamento não pode ser vazio.")
    @Column(name = "descricao", nullable = false)
    private String descricao;

    public Pagamento(@NotBlank(message = "CPF não pode ser vazio.") String cpf,
            @NotBlank(message = "Numero do cartão não pode ser vazio.") String numero,
            @NotBlank(message = "Data de validade do cartão não pode ser vazio.") Date data_validade,
            @NotBlank(message = "CVV do cartão não pode ser vazio.") String cvv,
            @NotBlank(message = "Valor do pagamento não pode ser vazio.") Double valor) {
        this.cpf = cpf;
        this.numero = numero;
        this.data_validade = data_validade;
        this.cvv = cvv;
        this.valor = valor;
    }


}
