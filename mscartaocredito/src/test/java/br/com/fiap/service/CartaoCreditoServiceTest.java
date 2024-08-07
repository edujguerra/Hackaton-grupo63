package br.com.fiap.service;

import br.com.fiap.infra.exception.LimiteCartoesException;
import br.com.fiap.infra.security.SecurityFilter;
import br.com.fiap.model.CartaoCredito;
import br.com.fiap.model.CartaoCreditoDTO;
import br.com.fiap.repository.CartaoCreditoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CartaoCreditoServiceTest {

    @InjectMocks
    private CartaoCreditoServiceImpl cartaoCreditoService;

    @Mock
    private CartaoCreditoRepository cartaoCreditoRepository;

    @Mock
    private SecurityFilter securityFilter;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ModelMapper modelMapper;

//    @BeforeEach
//    public void setUp() {
//        // Set up any necessary configurations or defaults here
//    }

    @Test
    public void testGerarCartaoCreditoCampoVazio() {

        CartaoCreditoDTO dto = new CartaoCreditoDTO();

        // Chama o método e captura a exceção
        Throwable thrown = catchThrowable(() -> cartaoCreditoService.gerarCartaoCredito(dto));

        // Verifica que a exceção lançada é a esperada
        assertThat(thrown).isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Registro de Cartão de Credito com campo vazio");
    }

    @Test
    public void testGerarCartaoCreditoClienteExistenteLimiteCartoes() {

        CartaoCreditoDTO dto = new CartaoCreditoDTO(1L, "12345678901", 5000.0, "1234567890123456",
                new Date(), "123");

        when(restTemplate.exchange(any(), any(Class.class)))
                .thenReturn(new ResponseEntity<>("Response Body", HttpStatus.OK));
        when(cartaoCreditoRepository.findByCpf(dto.getCpf()))
                .thenReturn(List.of(new CartaoCredito(), new CartaoCredito()));

        // Chama o método e captura a exceção
        Throwable thrown = catchThrowable(() -> cartaoCreditoService.gerarCartaoCredito(dto));

        // Verifica que a exceção lançada é a esperada
        assertThat(thrown).isInstanceOf(LimiteCartoesException.class)
                .hasMessageContaining("Cliente não pode ter mais que dois cartoes cadastrados!");
    }

    @Test
    public void testGerarCartaoCreditoSucesso() {

        CartaoCreditoDTO dto = new CartaoCreditoDTO(1L, "12345678901", 5000.0, "1234567890123456",
                new Date(), "123");

        // Mock de verificarClienteExistente
        when(securityFilter.getTokenBruto()).thenReturn("fake-token");
        when(restTemplate.exchange(any(RequestEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        // Mock do mapeamento e do save
        CartaoCredito cartaoCredito = cartaoCreditoService.toCartaoCredito(dto);
//        when(modelMapper.map(dto, CartaoCredito.class)).thenReturn(cartaoCredito);
        when(cartaoCreditoRepository.save(any())).thenReturn(cartaoCredito);

        CartaoCredito result = cartaoCreditoService.gerarCartaoCredito(dto);

        // Verifica o resultado
        assertThat(result).isNotNull();
        assertEquals(cartaoCredito, result);
    }

    @Test
    public void testVerificarClienteExistenteClienteNaoEncontrado() {
        // Mock do retorno da chamada para o cliente
        when(securityFilter.getTokenBruto()).thenReturn("fake-token");
        when(restTemplate.exchange(any(RequestEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        // Chama o método e captura a exceção
        Throwable thrown = catchThrowable(() -> cartaoCreditoService.verificarClienteExistente("12345678901"));

        // Verifica que a exceção lançada é a esperada
        assertThat(thrown).isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Cliente não encontrado");
    }

    @Test
    public void testObterCartoesPorCpf_CartoesEncontrados() {
        // Dados de teste
        String cpf = "12345678900";
        CartaoCredito cartao1 = new CartaoCredito();
        CartaoCredito cartao2 = new CartaoCredito();
        List<CartaoCredito> cartoes = new ArrayList<>();
        cartoes.add(cartao1);
        cartoes.add(cartao2);

        // Configuração do mock
        when(cartaoCreditoRepository.findByCpf(cpf)).thenReturn(cartoes);

        // Execução do método a ser testado
        List<CartaoCredito> resultado = cartaoCreditoService.obterCartoesPorCpf(cpf);

        // Verificação dos resultados
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertTrue(resultado.contains(cartao1));
        assertTrue(resultado.contains(cartao2));
    }

    @Test
    public void testObterCartoesPorCpf_NenhumCartaoEncontrado() {
        // Dados de teste
        String cpf = "12345678900";
        List<CartaoCredito> cartoes = Collections.emptyList();

        // Configuração do mock
        when(cartaoCreditoRepository.findByCpf(cpf)).thenReturn(cartoes);

        // Execução e verificação do método a ser testado
        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            cartaoCreditoService.obterCartoesPorCpf(cpf);
        });

        assertEquals("Não existem Cartao de Credito cadastrado para CPF solicitado", exception.getMessage());
    }
}
