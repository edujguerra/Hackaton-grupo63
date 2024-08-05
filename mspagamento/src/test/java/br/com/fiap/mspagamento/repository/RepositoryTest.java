package br.com.fiap.mspagamento.repository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.fiap.mspagamento.model.entity.Pagamento;

public class RepositoryTest {
        @Mock
        private PagamentoRepository pagamentoRepository;

        public RepositoryTest() {
            MockitoAnnotations.openMocks(this);
        }

        @Test
        public void test_retrieve_pagamento_by_valid_cpf() {
            String validCpf = "12345678901";
            Pagamento pagamento = new Pagamento();
            when(pagamentoRepository.findFirstByCpf(validCpf)).thenReturn(Optional.of(pagamento));

            Optional<Pagamento> result = pagamentoRepository.findFirstByCpf(validCpf);

            assertTrue(result.isPresent());
        }

        @Test
        public void test_handle_null_cpf_input_gracefully() {
            when(pagamentoRepository.findFirstByCpf(null)).thenReturn(Optional.empty());

            Optional<Pagamento> result = pagamentoRepository.findFirstByCpf(null);

            assertFalse(result.isPresent());
        }
}
