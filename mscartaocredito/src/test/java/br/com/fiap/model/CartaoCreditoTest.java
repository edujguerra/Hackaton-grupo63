package br.com.fiap.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CartaoCreditoTest {

    @Test
    public void testCartaoCreditoGettersAndSetters() {

        CartaoCredito cartaoCredito = new CartaoCredito();

        Long id = 1L;
        String cpf = "12345678901";
        Double limite = 5000.0;
        String numero = "1234567890123456";
        Date dataValidade = new Date();
        String cvv = "123";

        cartaoCredito.setId(id);
        cartaoCredito.setCpf(cpf);
        cartaoCredito.setLimite(limite);
        cartaoCredito.setNumero(numero);
        cartaoCredito.setData_validade(dataValidade);
        cartaoCredito.setCvv(cvv);

        // Testar se os valores são retornados corretamente pelos getters
        Assertions.assertThat(cartaoCredito.getId()).isEqualTo(id);
        Assertions.assertThat(cartaoCredito.getCpf()).isEqualTo(cpf);
        Assertions.assertThat(cartaoCredito.getLimite()).isEqualTo(limite);
        Assertions.assertThat(cartaoCredito.getNumero()).isEqualTo(numero);
        Assertions.assertThat(cartaoCredito.getData_validade()).isEqualTo(dataValidade);
        Assertions.assertThat(cartaoCredito.getCvv()).isEqualTo(cvv);
    }

    @Test
    public void testCartaoCreditoNoArgsConstructor() {

        CartaoCredito cartaoCredito = new CartaoCredito();

        Assertions.assertThat(cartaoCredito).isNotNull();
    }

    @Test
    public void testCartaoCreditoAllArgsConstructor() {

        Long id = 1L;
        String cpf = "12345678901";
        Double limite = 5000.0;
        String numero = "1234567890123456";
        Date dataValidade = new Date();
        String cvv = "123";

        CartaoCredito cartaoCredito = new CartaoCredito(id, cpf, limite, numero, dataValidade, cvv);

        // Testar se os valores são retornados corretamente pelos getters
        Assertions.assertThat(cartaoCredito.getId()).isEqualTo(id);
        Assertions.assertThat(cartaoCredito.getCpf()).isEqualTo(cpf);
        Assertions.assertThat(cartaoCredito.getLimite()).isEqualTo(limite);
        Assertions.assertThat(cartaoCredito.getNumero()).isEqualTo(numero);
        Assertions.assertThat(cartaoCredito.getData_validade()).isEqualTo(dataValidade);
        Assertions.assertThat(cartaoCredito.getCvv()).isEqualTo(cvv);
    }

    @Test
    public void test_equals_same_instance() {
        CartaoCredito cartao = new CartaoCredito(1L, "12345678901", 5000.0, "1234-5678-9012-3456", new Date(), "123");
        assertTrue(cartao.equals(cartao));
    }

    @Test
    public void test_consistent_hash_for_same_state() {
        CartaoCredito cartao1 = new CartaoCredito(1L, "12345678901", 5000.0, "1234-5678-9012-3456", new Date(), "123");
        CartaoCredito cartao2 = new CartaoCredito(1L, "12345678901", 5000.0, "1234-5678-9012-3456", new Date(), "123");
        int hash1 = cartao1.hashCode();
        int hash2 = cartao2.hashCode();
        assertEquals(hash1, hash2);
    }
}
