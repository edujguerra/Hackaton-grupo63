package br.com.fiap.mspagamento.service;

import br.com.fiap.mspagamento.infra.security.SecurityFilter;
import br.com.fiap.mspagamento.model.entity.Pagamento;
import br.com.fiap.mspagamento.model.enums.MetodoPagamento;
import br.com.fiap.mspagamento.model.enums.StatusPagamento;
import br.com.fiap.mspagamento.model.response.PagamentoResponse;
import br.com.fiap.mspagamento.repository.PagamentoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static br.com.fiap.mspagamento.service.PagamentoService.toPagamentoResponse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

        Pagamento pagamento1 = new Pagamento("12345678900", "1111222233334444", new Date(), "123", 100.0);
        Pagamento pagamento2 = new Pagamento("09876543211", "5555666677778888", new Date(), "456", 200.0);

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
}
