package br.com.fiap.mscliente.service;

import br.com.fiap.mscliente.model.Cliente;
import br.com.fiap.mscliente.repository.ClienteRepository;
import br.com.fiap.mscliente.utils.ClienteHelper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ClienteServiceTest {

    private ClienteService clienteService;

    @Mock
    private ClienteRepository clienteRepository;

    AutoCloseable openMocks;

    @BeforeEach
    void setup() {

        openMocks = MockitoAnnotations.openMocks(this);
        clienteService= new ClienteService(clienteRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Test
    void RegistrarCliente() throws Exception {

        // Arrange
        Cliente cliente = ClienteHelper.gerarCliente();
        cliente.setId(400);
        Mockito.when(clienteRepository.save(ArgumentMatchers.any(Cliente.class)))
                .thenAnswer(i -> i.getArgument(0));

        // Act
        Cliente response = (Cliente) clienteService.salvar(cliente).getBody();

        // Assert
        assertThat(response)
                .isInstanceOf(Cliente.class)
                .isNotNull()
                .isEqualTo(cliente);
        assertThat(response.getNome())
                .isEqualTo(cliente.getNome());
    }

    @Test
    void ListarUmCliente(){

        // Arrange
        int id = 100;
        Cliente cliente = ClienteHelper.gerarCliente();
        cliente.setId(id);
        Mockito.when(clienteRepository.findById(ArgumentMatchers.any(Integer.class)))
                .thenReturn(Optional.of(cliente));

        // Act
        ResponseEntity<?> resposta = clienteService.buscarUm(id);
        Cliente clienteArmazenado = (Cliente) resposta.getBody();

        // Assert
        Assertions.assertThat(clienteArmazenado)
                .isInstanceOf(Cliente.class)
                .isNotNull()
                .isEqualTo(cliente);
        Assertions.assertThat(clienteArmazenado)
                .extracting(Cliente::getId)
                .isEqualTo(cliente.getId());
    }

}
