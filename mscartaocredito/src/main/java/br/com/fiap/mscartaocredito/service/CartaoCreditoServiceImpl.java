package br.com.fiap.mscartaocredito.service;

import br.com.fiap.mscartaocredito.infra.exception.LimiteCartoesException;
import br.com.fiap.mscartaocredito.infra.security.SecurityFilter;
import br.com.fiap.mscartaocredito.model.CartaoCredito;
import br.com.fiap.mscartaocredito.model.CartaoCreditoDTO;
import br.com.fiap.mscartaocredito.repository.CartaoCreditoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
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

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ModelMapper modelMapper;

    public CartaoCreditoServiceImpl(CartaoCreditoRepository cartaoCreditoRepository, SecurityFilter securityFilter,
                                    RestTemplate restTemplate, ObjectMapper objectMapper, ModelMapper modelMapper) {
        this.cartaoCreditoRepository = cartaoCreditoRepository;
        this.securityFilter = securityFilter;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.modelMapper = modelMapper;
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

    private ResponseEntity<?> validaCampoVazio(CartaoCreditoDTO cartaoCreditoDTO) {

        if( cartaoCreditoDTO.getId() == null ||
                isEmpty(cartaoCreditoDTO.getCpf()) ||
                cartaoCreditoDTO.getLimite() == null ||
                isEmpty(cartaoCreditoDTO.getNumero()) ||
                cartaoCreditoDTO.getData_validade() == null ||
                isEmpty(cartaoCreditoDTO.getCvv())
        ) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Campo não pode ser vazio.");
        }
        return ResponseEntity.ok(cartaoCreditoDTO);
    }

    private static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    protected CartaoCredito toCartaoCredito(CartaoCreditoDTO dto) {
//        return modelMapper.map(dto, CartaoCredito.class);
        CartaoCredito cartaoCredito = new CartaoCredito(
                dto.getId(), dto.getCpf(), dto.getLimite(), dto.getNumero(), dto.getData_validade(), dto.getCvv()
        );
        return cartaoCredito;
    }

    private CartaoCreditoDTO toDTO(CartaoCredito cartaoCredito) {
        return modelMapper.map(cartaoCredito, CartaoCreditoDTO.class);
    }

    protected Boolean verificarClienteExistente(String cpf) {

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", securityFilter.getTokenBruto());

        //TODO: Criar no msCliente o endpoint que busca cliente por cpf (atualmente tem o que busca por id)
        URI uri = UriComponentsBuilder.fromUriString("http://msclientes:8082/api/clientes/{cpf}")
                .buildAndExpand(cpf)
                .toUri();

        RequestEntity<Object> request = new RequestEntity<>(headers, HttpMethod.GET, uri);
        ResponseEntity<String> response = restTemplate.exchange(request, String.class);

        if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
            throw new NoSuchElementException("Cliente não encontrado. Verifique cadastro do cliente.");
        } //else {
//            try {
//                JsonNode produtoJson = objectMapper.readTree(response.getBody());
//                String nome = produtoJson.get("nome").asText();
//            } catch (IOException e) {
//                throw new RegraNegocioException("Erro no metodo verificarClienteExistente");
//            }
//        }
        return true;
    }

}
