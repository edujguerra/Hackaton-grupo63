package br.com.fiap.mspagamento.controller;

import br.com.fiap.mspagamento.model.entity.Pagamento;
import br.com.fiap.mspagamento.model.enums.StatusPagamento;
import br.com.fiap.mspagamento.model.response.PagamentoResponse;
import br.com.fiap.mspagamento.service.PagamentoService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ControllerTest {

    @Test
    public void test_successful_payment_registration() throws Exception {
        PagamentoService pagamentoService = Mockito.mock(PagamentoService.class);
        PagamentoController pagamentoController = new PagamentoController(pagamentoService);

        Pagamento pagamento = new Pagamento("12345678900", "1234567890123456", YearMonth.now(), "123", 100.0);
        Pagamento novoPagamento = new Pagamento("12345678900", "1234567890123456", YearMonth.now(), "123", 100.0);
        novoPagamento.setId(1L);

        Mockito.when(pagamentoService.realizarPagamento(Mockito.any(Pagamento.class))).thenReturn(novoPagamento);

        ResponseEntity<?> response = pagamentoController.registrarPagamento(pagamento);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertTrue(response.getBody().toString().contains("chave_pagamento:1"));
    }

    @Test
    public void test_retrieve_payments_successfully_with_valid_cpf() {
        PagamentoService pagamentoService = Mockito.mock(PagamentoService.class);
        PagamentoController pagamentoController = new PagamentoController(pagamentoService);

        String cpf = "12345678900";
        List<PagamentoResponse> pagamentos = new ArrayList<>();
        pagamentos.add(new PagamentoResponse("Descricao", "Metodo", StatusPagamento.A, 100.0));

        Mockito.when(pagamentoService.obterPagamentosPorCPF(cpf)).thenReturn(pagamentos);

        ResponseEntity<?> response = pagamentoController.obterPagamentosPorCPF(cpf);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(pagamentos, response.getBody());
    }

    // Returns a list of all Pagamento objects
    @Test
    public void test_listar_pagamentos_returns_all_pagamentos() {
        PagamentoService pagamentoService = mock(PagamentoService.class);
        PagamentoController pagamentoController = new PagamentoController();
        ReflectionTestUtils.setField(pagamentoController, "pagamentoService", pagamentoService);

        List<Pagamento> expectedPagamentos = Arrays.asList(new Pagamento(), new Pagamento());
        when(pagamentoService.listarPagamentos()).thenReturn(expectedPagamentos);

        List<Pagamento> result = pagamentoController.listarPagamentos();

        assertEquals(expectedPagamentos, result);
        verify(pagamentoService, times(1)).listarPagamentos();
    }

    // Retrieve a payment by a valid ID
    @Test
    public void test_retrieve_payment_by_valid_id() {
        PagamentoService pagamentoService = mock(PagamentoService.class);
        PagamentoController pagamentoController = new PagamentoController();
        ReflectionTestUtils.setField(pagamentoController, "pagamentoService", pagamentoService);

        Integer validId = 1;
        Pagamento expectedPagamento = new Pagamento();
        when(pagamentoService.obterPagamentoPorId(validId)).thenReturn(expectedPagamento);

        Pagamento result = pagamentoController.obterPagamentoPorId(validId);

        assertNotNull(result);
        assertEquals(expectedPagamento, result);
    }
}
