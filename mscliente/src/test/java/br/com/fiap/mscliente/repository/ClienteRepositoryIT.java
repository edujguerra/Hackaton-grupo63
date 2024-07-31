package br.com.fiap.mscliente.repository;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
 class ClienteRepositoryIT {

    @Mock
    private ClienteRepository clienteRepository;

    @Test
    void devePermitirCriarTabela() {
        long totalTabelasCriada = clienteRepository.count();
        assertThat(totalTabelasCriada).isNotNegative();
    }
}
