package br.com.fiap.mspagamento.service;

import br.com.fiap.mspagamento.infra.security.SecurityFilter;
import br.com.fiap.mspagamento.model.DTO.CartaoDTO;
import br.com.fiap.mspagamento.model.entity.Pagamento;
import br.com.fiap.mspagamento.model.enums.MetodoPagamento;
import br.com.fiap.mspagamento.model.enums.StatusPagamento;
import br.com.fiap.mspagamento.model.response.PagamentoResponse;
import br.com.fiap.mspagamento.repository.PagamentoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.YearMonth;
import java.util.*;

import static br.com.fiap.mspagamento.service.PagamentoService.toPagamentoResponse;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ServiceTest {

    @Test
    public void test_listar_pagamentos_successfully() {
        PagamentoRepository pagamentoRepository = Mockito.mock(PagamentoRepository.class);
        SecurityFilter securityFilter = Mockito.mock(SecurityFilter.class);
        RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
        ObjectMapper objectMapper = Mockito.mock(ObjectMapper.class);

        PagamentoService pagamentoService = new PagamentoService(pagamentoRepository, securityFilter, restTemplate, objectMapper);

        List<Pagamento> expectedPagamentos = Arrays.asList(new Pagamento(), new Pagamento());
        Mockito.when(pagamentoRepository.findAll()).thenReturn(expectedPagamentos);

        List<Pagamento> actualPagamentos = pagamentoService.listarPagamentos();

        assertEquals(expectedPagamentos.size(), actualPagamentos.size());
        assertEquals(expectedPagamentos, actualPagamentos);
    }

    @Test
    public void test_converts_pagamento_to_pagamento_response_correctly() {
        Pagamento pagamento = new Pagamento();
        pagamento.setDescricao("Pagamento de teste");
        pagamento.setMetodoPagamento(MetodoPagamento.CC);
        pagamento.setStatusPagamento(StatusPagamento.A);
        pagamento.setValor(100.0);

        PagamentoResponse pagamentoResponse = toPagamentoResponse(pagamento);

        assertEquals("Pagamento de teste", pagamentoResponse.getDescricao());
        assertEquals(MetodoPagamento.CC, pagamentoResponse.getMetodoPagamento());
        assertEquals(StatusPagamento.A, pagamentoResponse.getStatusPagamento());
        assertEquals(100.0, pagamentoResponse.getValor());
    }


    @Test
    public void test_constructor_initializes_pagamentoRepository_correctly() {
        PagamentoRepository pagamentoRepository = Mockito.mock(PagamentoRepository.class);
        SecurityFilter securityFilter = Mockito.mock(SecurityFilter.class);
        RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
        ObjectMapper objectMapper = Mockito.mock(ObjectMapper.class);

        PagamentoService pagamentoService = new PagamentoService(pagamentoRepository, securityFilter, restTemplate, objectMapper);

        assertNotNull(pagamentoService);
        assertNotNull(pagamentoService.pagamentoRepository);
    }

    @Test
    public void test_listar_pagamentos_returns_all_pagamentos() {
        PagamentoRepository pagamentoRepository = Mockito.mock(PagamentoRepository.class);
        SecurityFilter securityFilter = Mockito.mock(SecurityFilter.class);
        RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
        ObjectMapper objectMapper = Mockito.mock(ObjectMapper.class);

        PagamentoService pagamentoService = new PagamentoService(pagamentoRepository, securityFilter, restTemplate, objectMapper);

        Pagamento pagamento1 = new Pagamento("12345678900", "1111222233334444", YearMonth.now(), "123", 100.0);
        Pagamento pagamento2 = new Pagamento("09876543211", "5555666677778888",  YearMonth.now(), "456", 200.0);

        List<Pagamento> pagamentos = Arrays.asList(pagamento1, pagamento2);
        Mockito.when(pagamentoRepository.findAll()).thenReturn(pagamentos);

        List<Pagamento> result = pagamentoService.listarPagamentos();

        Assertions.assertEquals(2, result.size());
        Assertions.assertTrue(result.contains(pagamento1));
        Assertions.assertTrue(result.contains(pagamento2));
    }

    @Test
    public void test_valid_pagamento_id_returns_pagamento() {
        PagamentoRepository pagamentoRepository = Mockito.mock(PagamentoRepository.class);
        SecurityFilter securityFilter = Mockito.mock(SecurityFilter.class);
        RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
        ObjectMapper objectMapper = Mockito.mock(ObjectMapper.class);

        PagamentoService pagamentoService = new PagamentoService(pagamentoRepository, securityFilter, restTemplate, objectMapper);

        Pagamento expectedPagamento = new Pagamento();
        expectedPagamento.setId(1L);

        Mockito.when(pagamentoRepository.findById(1)).thenReturn(Optional.of(expectedPagamento));

        Pagamento result = pagamentoService.obterPagamentoPorId(1);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedPagamento.getId(), result.getId());
    }

    @Test
    public void test_converts_pagamento_to_pagamentoresponse_correctly() {
        Pagamento pagamento = new Pagamento();
        pagamento.setDescricao("Pagamento de teste");
        pagamento.setMetodoPagamento(MetodoPagamento.CC);
        pagamento.setStatusPagamento(StatusPagamento.A);
        pagamento.setValor(100.0);

        PagamentoResponse pagamentoResponse = toPagamentoResponse(pagamento);

        assertEquals("Pagamento de teste", pagamentoResponse.getDescricao());
        assertEquals(MetodoPagamento.CC, pagamentoResponse.getMetodoPagamento());
        assertEquals(StatusPagamento.A, pagamentoResponse.getStatusPagamento());
        assertEquals(100.0, pagamentoResponse.getValor());
    }

    @Test
    public void test_retrieve_pagamentos_successfully_with_valid_cpf() {
        // Arrange
        String cpf = "12345678900";
        Pagamento pagamento = new Pagamento();
        pagamento.setCpf(cpf);
        pagamento.setDescricao("Pagamento Teste");
        pagamento.setMetodoPagamento(MetodoPagamento.CC);
        pagamento.setStatusPagamento(StatusPagamento.A);
        pagamento.setValor(100.0);

        List<Pagamento> pagamentos = Arrays.asList(pagamento);
        PagamentoRepository pagamentoRepository = Mockito.mock(PagamentoRepository.class);
        Mockito.when(pagamentoRepository.findByCpf(cpf)).thenReturn(Optional.of(pagamentos.toArray(new Pagamento[0])));

        PagamentoService pagamentoService = new PagamentoService(pagamentoRepository, null, null, null);

        // Act
        List<PagamentoResponse> result = pagamentoService.obterPagamentosPorCPF(cpf);

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Pagamento Teste", result.get(0).getDescricao());
    }

        @Test
    public void test_valid_cpf_returns_cartoes() throws Exception {
        // Arrange
        String validCpf = "12345678900";
        CartaoDTO[] cartaoArray = { new CartaoDTO(1L, validCpf, 5000.0, "1234-5678-9012-3456", new Date(), "123") };
        List<CartaoDTO> expectedCartoes = Arrays.asList(cartaoArray);

        SecurityFilter securityFilter = mock(SecurityFilter.class);
        when(securityFilter.getTokenBruto()).thenReturn("Bearer token");

        RestTemplate restTemplate = mock(RestTemplate.class);
        ResponseEntity<CartaoDTO[]> responseEntity = new ResponseEntity<>(cartaoArray, HttpStatus.OK);
        when(restTemplate.exchange(any(RequestEntity.class), eq(CartaoDTO[].class))).thenReturn(responseEntity);

        PagamentoService pagamentoService = new PagamentoService();
        pagamentoService.securityFilter = securityFilter;
        pagamentoService.restTemplate = restTemplate;

        // Act
        List<CartaoDTO> actualCartoes = pagamentoService.obterCartoes(validCpf);

        // Assert
        assertEquals(expectedCartoes, actualCartoes);
    }

        @Test
    public void test_server_error_throws_exception() {
        // Arrange
        String validCpf = "12345678900";

        SecurityFilter securityFilter = mock(SecurityFilter.class);
        when(securityFilter.getTokenBruto()).thenReturn("Bearer token");

        RestTemplate restTemplate = mock(RestTemplate.class);
        when(restTemplate.exchange(any(RequestEntity.class), eq(CartaoDTO[].class)))
            .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        PagamentoService pagamentoService = new PagamentoService();
        pagamentoService.securityFilter = securityFilter;
        pagamentoService.restTemplate = restTemplate;

        // Act & Assert
        assertThrows(HttpServerErrorException.class, () -> {
            pagamentoService.obterCartoes(validCpf);
        });
    }

        @Test
    public void test_valid_card_details_pass_validation() throws Exception {
        PagamentoService pagamentoService = new PagamentoService();
        Pagamento pagamento = new Pagamento("12345678900", "1234567890123456",  YearMonth.now(), "123", 100.0);
        CartaoDTO cartaoDTO = new CartaoDTO(1L, "12345678900", 200.0, "1234567890123456",  new Date(System.currentTimeMillis() + 100000000), "123");
        List<CartaoDTO> cartoes = Arrays.asList(cartaoDTO);

        PagamentoService pagamentoServiceSpy = Mockito.spy(pagamentoService);
        Mockito.doReturn(cartoes).when(pagamentoServiceSpy).obterCartoes("12345678900");

        assertDoesNotThrow(() -> pagamentoServiceSpy.validacaoCartao(pagamento));
    }

    @Test
    public void test_successful_payment_processing() throws Exception {
        PagamentoRepository pagamentoRepository = Mockito.mock(PagamentoRepository.class);
        PagamentoService pagamentoService = new PagamentoService(pagamentoRepository, null, null, null);
        String validCpf = "12345678900";

        CartaoDTO[] cartaoArray = { new CartaoDTO(1L, validCpf, 5000.0, "1234567890123456", new Date(System.currentTimeMillis() + 86400000) , "123") };
        SecurityFilter securityFilter = mock(SecurityFilter.class);
        when(securityFilter.getTokenBruto()).thenReturn("Bearer token");

        RestTemplate restTemplate = mock(RestTemplate.class);
        ResponseEntity<CartaoDTO[]> responseEntity = new ResponseEntity<>(cartaoArray, HttpStatus.OK);
        when(restTemplate.exchange(any(RequestEntity.class), eq(CartaoDTO[].class))).thenReturn(responseEntity);
        pagamentoService.securityFilter = securityFilter;
        pagamentoService.restTemplate = restTemplate;
        Pagamento pagamento = new Pagamento( validCpf, "1234567890123456", YearMonth.now(), "123", 100.0);
        Pagamento pagamento2 = new Pagamento(validCpf, "1234567890123456",YearMonth.now(), "123", 1020.0);
        pagamento2.setStatusPagamento(StatusPagamento.A);
        pagamento2.setDescricao("registro de pagamento");
        pagamento2.setMetodoPagamento(MetodoPagamento.CC);
        Pagamento[] pag= {new Pagamento(pagamento)};
        Mockito.when(pagamentoRepository.findByCpf(pagamento.getCpf())).thenReturn(Optional.of(pag));
        Mockito.when(pagamentoRepository.save(pagamento2)).thenReturn(pagamento2);

        Pagamento result = pagamentoService.realizarPagamento(pagamento2);
    
        assertNotNull(result);
        assertEquals(StatusPagamento.A, result.getStatusPagamento());
        Mockito.verify(pagamentoRepository).save(pagamento2);
    }
    
}
