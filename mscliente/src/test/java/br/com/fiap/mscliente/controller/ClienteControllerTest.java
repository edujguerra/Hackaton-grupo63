package br.com.fiap.mscliente.controller;

import br.com.fiap.mscliente.infra.security.TokenService;
import br.com.fiap.mscliente.model.Cliente;
import br.com.fiap.mscliente.service.ClienteService;
import br.com.fiap.mscliente.utils.ClienteHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ClienteControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ClienteService clienteService;

    AutoCloseable mock;

    @BeforeEach
    void setup() {
        mock = MockitoAnnotations.openMocks(this);
        ClienteController clienteController = new ClienteController(clienteService);

        mockMvc = MockMvcBuilders.standaloneSetup(clienteController)
                .build();

    }

    @AfterEach
    void tearDown() throws Exception {
        mock.close();
    }

    @MockBean
    private TokenService tokenService;

    @Test
    void RegistrarCliente() throws Exception {
        //arrange
        Cliente cliente = ClienteHelper.gerarCliente();

        //act & assert
        ResultActions result = mockMvc.perform(post("/api/cliente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ClienteHelper.asJsonString(cliente)));

        result.andExpect(status().isOk());
        verify(clienteService, times(1)).salvar(any(Cliente.class));
    }

    @Test
    void ListarUmCliente() throws Exception {
        Integer id = 1;
        Cliente cliente = ClienteHelper.gerarCliente();
        cliente.setId(id);

        ResultActions result = mockMvc.perform(get("/api/cliente/{id}",id)
                .contentType(MediaType.APPLICATION_JSON)
        );

        result.andExpect(status().isOk());
        verify(clienteService, times(1)).buscarUm(any(Integer.class));
    }

    @Test
    public void test_returns_200_ok_for_valid_id() {
        ClienteService service = mock(ClienteService.class);
        ClienteController controller = new ClienteController(service);
        Cliente cliente = new Cliente();
        cliente.setId(1);
        when(service.buscarUm(1)).thenReturn(ResponseEntity.ok(cliente));

        ResponseEntity<Object> response = controller.buscarUm(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(cliente, response.getBody());
    }

    @Test
    public void test_valid_cpf_returns_client_data() {
        ClienteService service = mock(ClienteService.class);
        ClienteController controller = new ClienteController(service);
        String validCpf = "12345678901";
        Cliente cliente = new Cliente();
        cliente.setCpf(validCpf);
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok(cliente);

        when(service.buscarPorCPF(validCpf)).thenReturn(expectedResponse);

        ResponseEntity<Object> response = controller.buscarPorCPF(validCpf);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(cliente, response.getBody());
    }
}
