
package br.com.fiap.mscliente.service;

import br.com.fiap.mscliente.model.Cliente;
import br.com.fiap.mscliente.repository.ClienteRepository;
import br.com.fiap.mscliente.utils.ClienteHelper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ClienteServiceIT {
  @Mock
  private ClienteRepository avaliacaoRepository;

  @Autowired
  private ClienteService avaliacaoService;

  AutoCloseable openMocks;

  @BeforeEach
  void setUp() {
    openMocks = MockitoAnnotations.openMocks(this);
    avaliacaoService = new ClienteService(avaliacaoRepository);
  }

  @AfterEach
  void tearDown() throws Exception {
    openMocks.close();
  }

  @Test
  void devePermitirRegistrarAvaliacao() {
    var avaliacao = ClienteHelper.gerarCliente();
    when(avaliacaoRepository.save(any(Cliente.class)))
            .thenAnswer(i -> i.getArgument(0));

    var avaliacaoArmazenada = avaliacaoService.salvar(avaliacao);

    assertThat(avaliacaoArmazenada)
            .isInstanceOf(ResponseEntity.class)
            .isNotNull();

    verify(avaliacaoRepository, times(1)).save(avaliacao);
  }



}
