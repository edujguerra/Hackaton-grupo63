package br.com.fiap.mspagamento.service;

import java.io.IOException;
import java.io.ObjectInputFilter.Status;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import br.com.fiap.mspagamento.infra.exception.LimiteException;
import br.com.fiap.mspagamento.infra.exception.PagamentoDuplicadoException;
import br.com.fiap.mspagamento.infra.exception.PagamentoException;
import br.com.fiap.mspagamento.infra.security.SecurityFilter;
import br.com.fiap.mspagamento.model.DTO.CartaoDTO;
import br.com.fiap.mspagamento.model.entity.Pagamento;
import br.com.fiap.mspagamento.model.enums.StatusPagamento;
import br.com.fiap.mspagamento.model.response.PagamentoResponse;
import br.com.fiap.mspagamento.model.response.RegistrarPagamentoResponse;
import br.com.fiap.mspagamento.repository.PagamentoRepository;
import lombok.NoArgsConstructor;

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
@NoArgsConstructor
public class PagamentoService {

    @Autowired
    PagamentoRepository pagamentoRepository;

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

    public Pagamento realizarPagamento (Pagamento pagamento){

        Pagamento meuPagamento = pagamentoRepository.findFirstByCpf(pagamento.getCpf()).orElse(null);
        //todo validar se a forma de verificar pagamento em duplicidade esta correto
        if(meuPagamento != null && meuPagamento.getData_validade().equals(pagamento.getData_validade())) {
            System.out.println("Pagamento duplicado!");
            throw new PagamentoDuplicadoException();
        }

        validacaoCartao(pagamento);
        pagamento.setStatusPagamento(StatusPagamento.A);

        return pagamentoRepository.save(pagamento);

//        RegistrarPagamentoResponse pagamentoResponse = toRegistrarPagamentoResponse(pagamento);

//        return pagamentoResponse;

    }

    public void validacaoCartao(Pagamento pagamento){
        
        List<CartaoDTO> cartoes = obterCartoes(pagamento.getCpf());

        CartaoDTO cartao = cartoes.stream().filter(c -> pagamento.getNumero().equals(c.getNumero())).findFirst().orElse(null);

        
        if (cartao == null) {
            throw new PagamentoException("Há um problema com o numero de cartão informado.");
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

        if (cartao.getLimite() < pagamento.getValor()) {
            throw new LimiteException("Limite excedido.");
        }
    }

    List<CartaoDTO> obterCartoes(String cpf) {
       
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", securityFilter.getTokenBruto());

        URI uri = UriComponentsBuilder.fromUriString("http://mscartaocredito:8082/api/cartao/cpf/{cpf}")
//        URI uri = UriComponentsBuilder.fromUriString("http://localhost:8082/api/cartao/cpf/{cpf}")
                .buildAndExpand(cpf)
                .toUri();

        RequestEntity<Object> request = new RequestEntity<>(headers, HttpMethod.GET, uri);
        try {
            ResponseEntity<CartaoDTO[]> response = restTemplate.exchange(request, CartaoDTO[].class);
            CartaoDTO[] cartaoCreditoArray = response.getBody();
            List<CartaoDTO> cartoes = Arrays.asList(cartaoCreditoArray);

            return cartoes;
        } catch (HttpServerErrorException e) {
            throw new HttpServerErrorException(e.getStatusCode());
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("Cartão de crédito não encontrado");
        }
    }

    public List<PagamentoResponse> obterPagamentosPorCPF(String cpf){
        List<Pagamento> pagamentos = Arrays.asList(pagamentoRepository.findByCpf(cpf).orElseThrow(() -> new PagamentoException("Pagamentos não encontrados")));

        return pagamentos.stream()
                .map(PagamentoService::toPagamentoResponse)
                .collect(Collectors.toList());
    }

    public static PagamentoResponse toPagamentoResponse(Pagamento pagamento){
        PagamentoResponse pagamentoResponse = new PagamentoResponse();
        pagamentoResponse.setDescricao(pagamento.getDescricao());
        pagamentoResponse.setMetodoPagamento(pagamento.getMetodoPagamento());
        pagamentoResponse.setStatusPagamento(pagamento.getStatusPagamento());
        pagamentoResponse.setValor(pagamento.getValor());
        return pagamentoResponse;
    }

    public static RegistrarPagamentoResponse toRegistrarPagamentoResponse(Pagamento pagamento) {
        RegistrarPagamentoResponse pagamentoResponse = new RegistrarPagamentoResponse();

        pagamentoResponse.setCpf(pagamento.getCpf());
        pagamentoResponse.setNumero(pagamento.getNumero());
        pagamentoResponse.setData_validade(pagamento.getData_validade());
        pagamentoResponse.setCvv(pagamento.getCvv());
        pagamentoResponse.setValor(pagamento.getValor());

        return pagamentoResponse;
    }

}
