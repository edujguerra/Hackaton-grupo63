package br.com.fiap.mscartaocredito.service;

import br.com.fiap.mscartaocredito.infra.exception.LimiteCartoesException;
import br.com.fiap.mscartaocredito.infra.security.SecurityFilter;
import br.com.fiap.mscartaocredito.model.CartaoCredito;
import br.com.fiap.mscartaocredito.model.CartaoCreditoDTO;
import br.com.fiap.mscartaocredito.repository.CartaoCreditoRepository;
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

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
}
