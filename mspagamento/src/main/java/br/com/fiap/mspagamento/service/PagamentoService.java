package br.com.fiap.mspagamento.service;

import java.io.IOException;
import java.io.ObjectInputFilter.Status;
import java.net.URI;
import java.time.YearMonth;
import java.time.ZoneId;
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
import br.com.fiap.mspagamento.model.enums.MetodoPagamento;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


@Service
@NoArgsConstructor
public class PagamentoService {

    @Autowired
    PagamentoRepository pagamentoRepository;

    @Autowired SecurityFilter securityFilter;

    @Autowired RestTemplate restTemplate;

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

    public Pagamento realizarPagamento (Pagamento pagamento) throws Exception{

        Pagamento[] meuPagamento = pagamentoRepository.findByCpf(pagamento.getCpf()).orElse(null);
        List<Pagamento> pagamentos = Arrays.asList(meuPagamento);

        //todo validar se a forma de verificar pagamento em duplicidade esta correto
        pagamentos.forEach(t -> {
            if(meuPagamento != null && t.getData_validade().equals(pagamento.getData_validade())&& t.getValor().equals(pagamento.getValor())) {
                throw new PagamentoDuplicadoException();
            }
        });


        validacaoCartao(pagamento);
        pagamento.setStatusPagamento(StatusPagamento.A);
        pagamento.setDescricao("registro de pagamento");
        pagamento.setMetodoPagamento(MetodoPagamento.CC);
        return pagamentoRepository.save(pagamento);

//        RegistrarPagamentoResponse pagamentoResponse = toRegistrarPagamentoResponse(pagamento);

//        return pagamentoResponse;

    }

    public void validacaoCartao(Pagamento pagamento) throws Exception{
    

        List<CartaoDTO> cartoes = obterCartoes(pagamento.getCpf());

        CartaoDTO cartao = cartoes.stream().filter(c -> pagamento.getNumero().equals(c.getNumero())).findFirst().orElse(null);
        if (cartao == null) {
            throw new PagamentoException("Há um problema com o numero de cartão informado.");
        }
        YearMonth yearMonth =
        YearMonth.from(cartao.getData_validade().toInstant()
                           .atZone(ZoneId.systemDefault())
                           .toLocalDate());

        if (!pagamento.getData_validade().equals(yearMonth)) {
            throw new PagamentoException("Há um problema com a data de validade informada.");
        }

        if (cartao.getData_validade() instanceof Date){
            Date dataValidade = (Date) cartao.getData_validade();
            if (dataValidade.before(new Date())){
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

    List<CartaoDTO> obterCartoes(String cpf) throws Exception {
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
            e.printStackTrace();
            throw new HttpServerErrorException(e.getStatusCode());
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            throw new NoSuchElementException("Cartão de crédito não encontrado");
        }catch (HttpClientErrorException e) {
            e.printStackTrace();
            throw new PagamentoException("Erro ao consultar cartões.");
        }catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage(),e.getCause());
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

}
