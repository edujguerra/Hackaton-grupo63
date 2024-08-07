package br.com.fiap.mspagamento.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;

import java.time.YearMonth;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import br.com.fiap.mspagamento.config.YearMonthDeserializer;
import br.com.fiap.mspagamento.model.enums.MetodoPagamento;
import br.com.fiap.mspagamento.model.enums.StatusPagamento;
import io.swagger.v3.oas.annotations.media.Schema;

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

    @NotNull(message = "Data de validade do cartão não pode ser nula.")
    @Column(name = "data_validade", nullable = false)
    @JsonDeserialize(using = YearMonthDeserializer.class)
    @Schema(description = "Ano e mês no formato MM/yy", example = "12/24")
    private YearMonth data_validade;

    @NotBlank(message = "CVV do cartão não pode ser vazio.")
    @Column(name = "cvv", nullable = false)
    private String cvv;

    @NotNull(message = "Valor do pagamento não pode ser nulo.")
    @Positive(message = "O valor deve ser positivo.")
    @Column(name = "valor", nullable = false)
    private Double valor;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusPagamento statusPagamento;

    @Enumerated(EnumType.STRING)
    @Column(name = "forma_pagamento", nullable = false)
    private MetodoPagamento metodoPagamento;

    @Column(name = "descricao", nullable = false)
    private String descricao;

    public Pagamento(@NotBlank(message = "CPF não pode ser vazio.") String cpf,
            @NotBlank(message = "Numero do cartão não pode ser vazio.") String numero,
            @NotNull(message = "Data de validade do cartão não pode ser vazio.") @NotNull(message = "Data de validade do cartão não pode ser nula.") YearMonth data_validade,
            @NotBlank(message = "CVV do cartão não pode ser vazio.") String cvv,
                     @NotNull(message = "Valor do pagamento não pode ser nulo.") @Positive(message = "O valor deve ser positivo.") Double valor) {
        this.cpf = cpf;
        this.numero = numero;
        this.data_validade = data_validade;
        this.cvv = cvv;
        this.valor = valor;
    }

    public Pagamento(Pagamento pagamento) {
this.cpf = pagamento.getCpf();
this.numero = pagamento.getNumero();
this.data_validade = pagamento.getData_validade();
this.cvv = pagamento.getCvv();
this.valor = pagamento.getValor();
}


}
