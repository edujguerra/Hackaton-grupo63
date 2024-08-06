package br.com.fiap.mspagamento.model;

import br.com.fiap.mspagamento.model.DTO.PagamentoDTO;
import br.com.fiap.mspagamento.model.response.RegistrarPagamentoResponse;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.Test;

import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ModelTest {
    @Test
    public void test_create_pagamento_dto_with_all_fields() {
        Long id = 1L;
        String cpf = "12345678901";
        String numero = "1234567890123456";
        Date dataValidade = new Date();
        String cvv = "123";
        Double valor = 100.0;

        PagamentoDTO pagamentoDTO = new PagamentoDTO(id, cpf, numero, dataValidade, cvv, valor);

        assertEquals(id, pagamentoDTO.getId());
        assertEquals(cpf, pagamentoDTO.getCpf());
        assertEquals(numero, pagamentoDTO.getNumero());
        assertEquals(dataValidade, pagamentoDTO.getData_validade());
        assertEquals(cvv, pagamentoDTO.getCvv());
        assertEquals(valor, pagamentoDTO.getValor());
    }

    @Test
    public void test_create_instance_with_all_fields_populated() {
        String cpf = "12345678901";
        String numero = "1234567890123456";
        Date dataValidade = new Date();
        String cvv = "123";
        Double valor = 100.0;

        RegistrarPagamentoResponse response = new RegistrarPagamentoResponse(cpf, numero, dataValidade, cvv, valor);

        assertEquals(cpf, response.getCpf());
        assertEquals(numero, response.getNumero());
        assertEquals(dataValidade, response.getData_validade());
        assertEquals(cvv, response.getCvv());
        assertEquals(valor, response.getValor());
    }
}
