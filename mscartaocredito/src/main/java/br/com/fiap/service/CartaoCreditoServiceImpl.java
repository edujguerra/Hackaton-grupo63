package br.com.fiap.service;

import br.com.fiap.infra.exception.LimiteCartoesException;
import br.com.fiap.infra.security.SecurityFilter;
import br.com.fiap.model.CartaoCredito;
import br.com.fiap.model.CartaoCreditoDTO;
import br.com.fiap.repository.CartaoCreditoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;


@Service
public class CartaoCreditoServiceImpl implements CartaoCreditoService{

    private static final Logger LOGGER = LoggerFactory.getLogger(CartaoCreditoServiceImpl.class);

    @Autowired
    private CartaoCreditoRepository cartaoCreditoRepository;
    @Autowired
    private SecurityFilter securityFilter;
    @Autowired
    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;

    public CartaoCreditoServiceImpl(CartaoCreditoRepository cartaoCreditoRepository, SecurityFilter securityFilter,
                                    RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.cartaoCreditoRepository = cartaoCreditoRepository;
        this.securityFilter = securityFilter;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public CartaoCredito gerarCartaoCredito(CartaoCreditoDTO cartaoCreditoDTO) {

        ResponseEntity<?> response = validaCampoVazio(cartaoCreditoDTO);
        if (!response.getStatusCode().equals(HttpStatus.OK)) {
            throw new NoSuchElementException("Registro de Cartão de Credito com campo vazio: " + response);
        }

        verificarClienteExistente(cartaoCreditoDTO.getCpf());

        List<CartaoCredito> cartoesCreditoExistente = cartaoCreditoRepository.findByCpf(cartaoCreditoDTO.getCpf());
        if (cartoesCreditoExistente.size() >= 2) {
            LOGGER.info("Cliente não pode ter mais que dois cartoes cadastrados!");
            throw new LimiteCartoesException("Cliente não pode ter mais que dois cartoes cadastrados!");
        }

        CartaoCredito cartaoCredito = toCartaoCredito(cartaoCreditoDTO);
        cartaoCreditoRepository.save(cartaoCredito);

        return cartaoCredito;
    }

    @Override
    public List<CartaoCredito> obterCartoesPorCpf(String cpf) {
        List<CartaoCredito> listaDeCartoes = cartaoCreditoRepository.findByCpf(cpf);
        if (listaDeCartoes.isEmpty()) {
            throw new NoSuchElementException("Não existem Cartao de Credito cadastrado para CPF solicitado");
        }
        return listaDeCartoes;
    }

    private ResponseEntity<?> validaCampoVazio(CartaoCreditoDTO cartaoCreditoDTO) {

        if(isEmpty(cartaoCreditoDTO.getCpf()) ||
                cartaoCreditoDTO.getLimite() == null ||
                isEmpty(cartaoCreditoDTO.getNumero()) ||
                cartaoCreditoDTO.getData_validade() == null ||
                isEmpty(cartaoCreditoDTO.getCvv()) ) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Campo não pode ser vazio.");
        }
        return ResponseEntity.ok(cartaoCreditoDTO);
    }

    private static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    protected CartaoCredito toCartaoCredito(CartaoCreditoDTO dto) {

        CartaoCredito cartaoCredito = new CartaoCredito(
                dto.getId(), dto.getCpf(), dto.getLimite(), dto.getNumero(), dto.getData_validade(), dto.getCvv()
        );
        return cartaoCredito;
    }

    protected Boolean verificarClienteExistente(String cpf) {

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", securityFilter.getTokenBruto());

        URI uri = UriComponentsBuilder.fromUriString("http://msclientes:8081/api/cliente/cpf/{cpf}")
//        URI uri = UriComponentsBuilder.fromUriString("http://127.0.0.1:8081/api/cliente/cpf/{cpf}")
                .buildAndExpand(cpf)
                .toUri();

        RequestEntity<Object> request = new RequestEntity<>(headers, HttpMethod.GET, uri);
        ResponseEntity<String> response = restTemplate.exchange(request, String.class);

        if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
            throw new NoSuchElementException("Cliente não encontrado. Verifique cadastro do cliente.");
        }
        return true;
    }

}
