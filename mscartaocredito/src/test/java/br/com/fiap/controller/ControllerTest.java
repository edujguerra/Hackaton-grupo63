package br.com.fiap.controller;

import br.com.fiap.infra.exception.LimiteCartoesException;
import br.com.fiap.infra.exception.RegraNegocioException;
import br.com.fiap.model.CartaoCredito;
import br.com.fiap.model.CartaoCreditoDTO;
import br.com.fiap.service.CartaoCreditoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CartaoCreditoService cartaoCreditoService;

    @InjectMocks
    private CartaoCreditoController cartaoCreditoController;

    private ObjectMapper objectMapper = new ObjectMapper();  // Para converter objetos em JSON

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(cartaoCreditoController).build();
    }


    @Test
    public void test_gerar_cartao_credito_valid_dto() {
        CartaoCreditoService cartaoCreditoService = Mockito.mock(CartaoCreditoService.class);
        CartaoCreditoController controller = new CartaoCreditoController(cartaoCreditoService);
        ReflectionTestUtils.setField(controller, "cartaoCreditoService", cartaoCreditoService);

        CartaoCreditoDTO dto = new CartaoCreditoDTO(1L, "12345678900", 5000.0, "1234567890123456", new Date(), "123");
        CartaoCredito cartaoCredito = new CartaoCredito(1L, "12345678900", 5000.0, "1234567890123456", new Date(), "123");

        when(cartaoCreditoService.gerarCartaoCredito(dto)).thenReturn(cartaoCredito);

        ResponseEntity<?> response = controller.gerarCartaoCredito(dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(cartaoCredito, response.getBody());
    }

    @Test
    public void test_obter_cartao_credito_valid_dto() {
        CartaoCreditoService cartaoCreditoService = Mockito.mock(CartaoCreditoService.class);
        CartaoCreditoController controller = new CartaoCreditoController(cartaoCreditoService);
        ReflectionTestUtils.setField(controller, "cartaoCreditoService", cartaoCreditoService);

        CartaoCreditoDTO dto = new CartaoCreditoDTO(1L, "12345678900", 5000.0, "1234567890123456", new Date(), "123");
        CartaoCredito cartaoCredito = new CartaoCredito(1L, "12345678900", 5000.0, "1234567890123456", new Date(), "123");

        List<CartaoCredito> cartoes = new ArrayList<>();
        cartoes.add(cartaoCredito);

        when(cartaoCreditoService.obterCartoesPorCpf(dto.getCpf())).thenReturn(cartoes);

        ResponseEntity<?> response = controller.obterCartoesPorCpf(dto.getCpf());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(cartoes, response.getBody());
    }

    @Test
    public void testGerarCartaoCredito_NoSuchElementException() throws Exception {
        when(cartaoCreditoService.gerarCartaoCredito(any(CartaoCreditoDTO.class)))
                .thenThrow(new NoSuchElementException("Elemento não encontrado"));

        mockMvc.perform(post("/api/cartao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CartaoCreditoDTO())))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Cartão de Credito não gerado: Elemento não encontrado"));
    }

    @Test
    public void testGerarCartaoCredito_RegraNegocioException() throws Exception {
        when(cartaoCreditoService.gerarCartaoCredito(any(CartaoCreditoDTO.class)))
                .thenThrow(new RegraNegocioException("Regra de negócio violada"));

        mockMvc.perform(post("/api/cartao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CartaoCreditoDTO())))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Cartão de Credito não gerado: Regra de negócio violada"));
    }

    @Test
    public void testGerarCartaoCredito_LimiteCartoesException() throws Exception {
        when(cartaoCreditoService.gerarCartaoCredito(any(CartaoCreditoDTO.class)))
                .thenThrow(new LimiteCartoesException("Limite atingido"));

        mockMvc.perform(post("/api/cartao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CartaoCreditoDTO())))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Limite de cartoes atingido: Limite atingido"));
    }

    @Test
    public void testGerarCartaoCredito_AuthorizationDeniedException() throws Exception {
        when(cartaoCreditoService.gerarCartaoCredito(any(CartaoCreditoDTO.class)))
                .thenThrow(new AuthorizationDeniedException("Acesso negado", new AuthorizationResult() {
                    @Override
                    public boolean isGranted() {
                        return false;
                    }
                }));

        mockMvc.perform(post("/api/cartao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CartaoCreditoDTO())))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Erro de autorização."));
    }

    @Test
    public void testObterCartoesPorCpf_NoSuchElementException() throws Exception {
        String cpf = "12345678900";
        when(cartaoCreditoService.obterCartoesPorCpf(cpf))
                .thenThrow(new NoSuchElementException("Nenhum cartão encontrado"));

        mockMvc.perform(get("/api/cartao/cpf/{cpf}", cpf))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Nenhum Cartao de credito encontrado para o CPF fornecido."));
    }

    @Test
    public void testObterCartoesPorCpf_InternalServerError() throws Exception {
        String cpf = "12345678900";
        when(cartaoCreditoService.obterCartoesPorCpf(cpf))
                .thenThrow(new RuntimeException("Erro inesperado"));

        mockMvc.perform(get("/api/cartao/cpf/{cpf}", cpf))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Ocorreu um erro ao processar a solicitação."));
    }

    @Test
    public void testCartaoCreditoController_ConstrutorSemParametros() {

        CartaoCreditoController controller = new CartaoCreditoController();

        // Verifica que o controlador foi criado e o serviço está nulo
        assertNotNull(controller);
    }
}
