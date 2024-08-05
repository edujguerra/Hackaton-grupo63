package br.com.fiap.mspagamento.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import br.com.fiap.mspagamento.infra.exception.LimiteException;
import br.com.fiap.mspagamento.infra.exception.PagamentoDuplicadoException;
import br.com.fiap.mspagamento.infra.exception.PagamentoException;
import br.com.fiap.mspagamento.model.entity.Pagamento;
import br.com.fiap.mspagamento.model.response.PagamentoResponse;
import br.com.fiap.mspagamento.service.PagamentoService;

public class ControllerTest {
    @Test
    public void test_retrieve_payments_successfully_with_valid_cpf() {
        PagamentoService pagamentoService = Mockito.mock(PagamentoService.class);
        PagamentoController pagamentoController = new PagamentoController();
        ReflectionTestUtils.setField(pagamentoController, "pagamentoService", pagamentoService);

        String validCpf = "12345678900";
        List<PagamentoResponse> expectedResponse = Arrays.asList(new PagamentoResponse());
        Mockito.when(pagamentoService.obterPagamentosPorCPF(validCpf)).thenReturn(expectedResponse);

        ResponseEntity<?> response = pagamentoController.obterPagamentosPorCPF(validCpf);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    public void test_handle_pagamento_exception_and_return_http_401_unauthorized() {
        PagamentoService pagamentoService = Mockito.mock(PagamentoService.class);
        PagamentoController pagamentoController = new PagamentoController();
        ReflectionTestUtils.setField(pagamentoController, "pagamentoService", pagamentoService);

        String invalidCpf = "00000000000";
        Mockito.when(pagamentoService.obterPagamentosPorCPF(invalidCpf)).thenThrow(new PagamentoException("Pagamentos não encontrados"));

        ResponseEntity<?> response = pagamentoController.obterPagamentosPorCPF(invalidCpf);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Pagamentos não encontrados", response.getBody());
    }  

    @Test
    public void test_successful_payment() {
        PagamentoService pagamentoService = mock(PagamentoService.class);
        PagamentoController pagamentoController = new PagamentoController(pagamentoService);
        Pagamento pagamento = new Pagamento();
        pagamento.setCpf("12345678900");
        pagamento.setData_validade(new Date());
        pagamento.setId(1L);
    
        when(pagamentoService.realizarPagamento(any(Pagamento.class))).thenReturn(pagamento);
    
        ResponseEntity<?> response = pagamentoController.registrarPagamento(pagamento);
    
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("chave_pagamento:" + pagamento.getId()));
    }

    @Test
    public void test_null_payment() {
        PagamentoService pagamentoService = mock(PagamentoService.class);
        PagamentoController pagamentoController = new PagamentoController(pagamentoService);
    
        ResponseEntity<?> response = pagamentoController.registrarPagamento(null);
    
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Erro interno de aplicação", response.getBody());
    }

    @Test
    public void test_payment_required() {
        PagamentoService pagamentoService = mock(PagamentoService.class);
        PagamentoController pagamentoController = new PagamentoController(pagamentoService);
        Pagamento pagamento1 = new Pagamento("12345678900", "1111222233334444", new Date(), "123", 100.0);
        when(pagamentoService.realizarPagamento(any(Pagamento.class))).thenThrow(new LimiteException("Limite excedido"));

        ResponseEntity<?> response = pagamentoController.registrarPagamento(pagamento1);
    
        assertEquals(HttpStatus.PAYMENT_REQUIRED, response.getStatusCode());
        assertEquals("Limite excedido", response.getBody());
    }

    @Test
    public void test_pagamento_exception() {
        PagamentoService pagamentoService = mock(PagamentoService.class);
        PagamentoController pagamentoController = new PagamentoController(pagamentoService);
        Pagamento pagamento1 = new Pagamento("12345678900", "1111222233334444", new Date(), "123", 100.0);

        when(pagamentoService.realizarPagamento(any(Pagamento.class))).thenThrow(new PagamentoException("Há um problema com o numero de cartão informado."));

        ResponseEntity<?> response = pagamentoController.registrarPagamento(pagamento1);
    
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Há um problema com o numero de cartão informado.", response.getBody());
    }

    @Test
    public void test_no_such_element() {
        PagamentoService pagamentoService = mock(PagamentoService.class);
        PagamentoController pagamentoController = new PagamentoController(pagamentoService);
        Pagamento pagamento1 = new Pagamento("12345678900", "1111222233334444", new Date(), "123", 100.0);

        when(pagamentoService.realizarPagamento(any(Pagamento.class))).thenThrow(new NoSuchElementException("Exception do registrar pagamento"));

        ResponseEntity<?> response = pagamentoController.registrarPagamento(pagamento1);
    
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Exception do registrar pagamento", response.getBody());
    }

    @Test
    public void test_null_payment_card() {
        PagamentoService pagamentoService = mock(PagamentoService.class);
        PagamentoController pagamentoController = new PagamentoController(pagamentoService);
        Pagamento pagamento1 = new Pagamento("12345678900", "1111222233334444", new Date(), "123", 100.0);

        when(pagamentoService.realizarPagamento(any(Pagamento.class))).thenThrow(new PagamentoDuplicadoException("Limite excedido"));

        ResponseEntity<?> response = pagamentoController.registrarPagamento(pagamento1);
    
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Limite excedido", response.getBody());
    }

}
