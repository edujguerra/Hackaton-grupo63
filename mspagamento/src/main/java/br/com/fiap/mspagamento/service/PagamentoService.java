package br.com.fiap.mspagamento.service;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import br.com.fiap.mspagamento.infra.exception.PagamentoDuplicadoException;
import br.com.fiap.mspagamento.infra.exception.PagamentoException;
import br.com.fiap.mspagamento.infra.security.SecurityFilter;
import br.com.fiap.mspagamento.model.CartaoDTO;
import br.com.fiap.mspagamento.model.Pagamento;
import br.com.fiap.mspagamento.model.PagamentoDTO;
import br.com.fiap.mspagamento.repository.PagamentoRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


@Service
public class PagamentoService {

    @Autowired
    private PagamentoRepository pagamentoRepository;

    @Autowired
    private SecurityFilter securityFilter;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public PagamentoService (PagamentoRepository pagamentoRepository,
            SecurityFilter securityFilter, RestTemplate restTemplate, ObjectMapper objectMapper){
        this.pagamentoRepository = pagamentoRepository;
        this.securityFilter = securityFilter;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public List<Pagamento> listarPagamentos() {
        return pagamentoRepository.findAll();
    }

    public Pagamento obterPagamentoPorId (Integer pagamentoId) {

        Pagamento pagamento = pagamentoRepository.findById(pagamentoId).orElse(null);
        if (pagamento != null) {
            return pagamento;
        } else {
            throw new NoSuchElementException("Pagamento com código {} não encontrado" + pagamentoId);
        }
    }

    public double obterLimiteCartaoCredito(String cpf){

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", securityFilter.getTokenBruto());

        URI uri = UriComponentsBuilder.fromUriString("http://mscartaocredito:8082/api/cartao/{cpf}")
                .buildAndExpand(cpf)
                .toUri();

        RequestEntity<Object> request = new RequestEntity<>(headers, HttpMethod.GET, uri);
        try {
            ResponseEntity<String> response = restTemplate.exchange(request, String.class);
            JsonNode cartaoCredito = objectMapper.readTree(response.getBody());
            double limite = cartaoCredito.get("limite").asDouble();
            return limite;
        } catch (HttpServerErrorException e) {
            throw new NoSuchElementException("Cartão de crédito não encontrado");
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("Cartão de crédito não encontrado");
        } catch(IOException e){
            throw new RuntimeException("Erro no método obter limite do cartão de crédito");
        }
    }

    public void verificarNumeroCartao (String cpf, String numero){
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", securityFilter.getTokenBruto());

        URI uri = UriComponentsBuilder.fromUriString("http://mscartaocredito:8082/api/cartao/{cpf}")
                .buildAndExpand(cpf)
                .toUri();


        RequestEntity<Object> request = new RequestEntity<>(headers, HttpMethod.GET, uri);
        try {
            ResponseEntity<String> response = restTemplate.exchange(request, String.class);
            JsonNode cartoesCredito = objectMapper.readTree(response.getBody());
            for (JsonNode cartaoCredito : cartoesCredito){
                //todo reverter
                if (!numero.equals(cartaoCredito.get("numero").asText())){
                    throw new PagamentoException("Há um problema com o numero de cartão informado.");
                }
            }
        } catch (HttpServerErrorException e) {
            throw new NoSuchElementException("Cartão de crédito não encontrado");
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("Cartão de crédito não encontrado");
        } catch(IOException e) {
            throw new RuntimeException("Erro no método verificar número do cartão");
        }
    }

    public void verificarValidadeCartao (String numero){
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", securityFilter.getTokenBruto());

        URI uri = UriComponentsBuilder.fromUriString("http://mscartaocredito:8082/api/cartao/{numero}")
                .buildAndExpand(numero)
                .toUri();


        RequestEntity<Object> request = new RequestEntity<>(headers, HttpMethod.GET, uri);
        try {
            ResponseEntity<String> response = restTemplate.exchange(request, String.class);
            JsonNode cartaoCredito = objectMapper.readTree(response.getBody());

            Object dataValidadeCartao = cartaoCredito.get("dataValidade").asText();
            if (dataValidadeCartao instanceof Date){
                Date dataValidade = (Date) dataValidadeCartao;
                if (!dataValidade.before(new Date())){
                    throw new PagamentoException("Validade do cartao excedida.");
                }
            }
        } catch (HttpServerErrorException e) {
            throw new NoSuchElementException("Cartão de crédito não encontrado");
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("Cartão de crédito não encontrado");
        } catch(IOException e) {
            throw new RuntimeException("Erro no método verificar número do cartão");
        }
    }

    public boolean verificarCvv (String numero){
        return true;
    }



    public Pagamento realizarPagamento (Pagamento pagamento){

        Pagamento meuPagamento = pagamentoRepository.findFirstByCpf(pagamento.getCpf()).orElse(null);
        //todo validar se a forma de verificar pagamento em duplicidade esta correto
        if(meuPagamento != null && meuPagamento.getData_validade().equals(pagamento.getData_validade())) {
            System.out.println("Pagamento duplicado!");
            throw new PagamentoDuplicadoException();
        }

        validacaoCartao(pagamento);

        return pagamentoRepository.save(pagamento);
    }

    public Pagamento toPagamento (PagamentoDTO pagamentoDTO) {
        return new Pagamento(
                pagamentoDTO.getId(),
                pagamentoDTO.getCpf(),
                pagamentoDTO.getNumero(),
                pagamentoDTO.getData_validade(),
                pagamentoDTO.getCvv(),
                pagamentoDTO.getValor()
        );
    }

    public void validacaoCartao(Pagamento pagamento){
        
        List<CartaoDTO> cartoes = obterCartoes(pagamento.getCpf());

        CartaoDTO cartao = cartoes.stream().filter(c -> pagamento.getNumero().equals(c.getNumero())).findFirst().orElse(null);

        
        if (cartao == null) {
            throw new PagamentoException("Há um problema com o numero de cartão informado.");
        }
        if (cartao.getLimite() < pagamento.getValor()) {
            throw new PagamentoException("Limite excedido.");
        }
        if (cartao.getData_validade() instanceof Date){
            Date dataValidade = (Date) cartao.getData_validade();
            if (!dataValidade.before(new Date())){
                throw new PagamentoException("Validade do cartao excedida.");
            }
        }
        if (!cartao.getCvv().equals(pagamento.getCvv())) {
            throw new PagamentoException("Codigo invalido.");
        }
    }

    private List<CartaoDTO> obterCartoes(String cpf) {
       
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", securityFilter.getTokenBruto());

        URI uri = UriComponentsBuilder.fromUriString("http://mscartaocredito:8082/api/cartao/cpf/{cpf}")
                .buildAndExpand(cpf)
                .toUri();

        RequestEntity<Object> request = new RequestEntity<>(headers, HttpMethod.GET, uri);
        try {
            ResponseEntity<CartaoDTO[]> response = restTemplate.exchange(request, CartaoDTO[].class);
            CartaoDTO[] cartaoCreditoArray = response.getBody();
            List<CartaoDTO> cartoes = Arrays.asList(cartaoCreditoArray);

            return cartoes;
        } catch (HttpServerErrorException e) {
            throw new NoSuchElementException("Cartão de crédito não encontrado");
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("Cartão de crédito não encontrado");
        }
    }

    public List<Pagamento> obterPagamentosPorCPF(String cpf){
        return Arrays.asList(pagamentoRepository.findByCpf(cpf).orElseThrow(() -> new RuntimeException("Pagamentos não encontrados")));
    }

}
