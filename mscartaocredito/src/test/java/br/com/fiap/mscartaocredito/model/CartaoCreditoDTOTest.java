package br.com.fiap.mscartaocredito.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Date;

public class CartaoCreditoDTOTest {

    @Test
    public void testCartaoCreditoDTOGettersAndSetters() {

        CartaoCreditoDTO dto = new CartaoCreditoDTO();

        Long id = 1L;
        String cpf = "12345678901";
        Double limite = 5000.0;
        String numero = "1234567890123456";
        Date dataValidade = new Date();
        String cvv = "123";

        dto.setId(id);
        dto.setCpf(cpf);
        dto.setLimite(limite);
        dto.setNumero(numero);
        dto.setData_validade(dataValidade);
        dto.setCvv(cvv);

        Assertions.assertThat(dto.getId()).isEqualTo(id);
        Assertions.assertThat(dto.getCpf()).isEqualTo(cpf);
        Assertions.assertThat(dto.getLimite()).isEqualTo(limite);
        Assertions.assertThat(dto.getNumero()).isEqualTo(numero);
        Assertions.assertThat(dto.getData_validade()).isEqualTo(dataValidade);
        Assertions.assertThat(dto.getCvv()).isEqualTo(cvv);
    }

    @Test
    public void testCartaoCreditoDTONoArgsConstructor() {

        CartaoCreditoDTO dto = new CartaoCreditoDTO();

        // Testar se a instância é criada corretamente (não deve lançar exceção)
        Assertions.assertThat(dto).isNotNull();
    }

    @Test
    public void testCartaoCreditoDTOAllArgsConstructor() {

        Long id = 1L;
        String cpf = "12345678901";
        Double limite = 5000.0;
        String numero = "1234567890123456";
        Date dataValidade = new Date();
        String cvv = "123";

        // Criar uma instância de CartaoCreditoDTO usando o construtor com todos os argumentos
        CartaoCreditoDTO dto = new CartaoCreditoDTO(id, cpf, limite, numero, dataValidade, cvv);

        Assertions.assertThat(dto.getId()).isEqualTo(id);
        Assertions.assertThat(dto.getCpf()).isEqualTo(cpf);
        Assertions.assertThat(dto.getLimite()).isEqualTo(limite);
        Assertions.assertThat(dto.getNumero()).isEqualTo(numero);
        Assertions.assertThat(dto.getData_validade()).isEqualTo(dataValidade);
        Assertions.assertThat(dto.getCvv()).isEqualTo(cvv);
    }
}
