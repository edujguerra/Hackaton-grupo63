package br.com.fiap.mspagamento.service;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import br.com.fiap.mspagamento.infra.exception.PagamentoDuplicadoException;
import br.com.fiap.mspagamento.infra.security.SecurityFilter;
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

    /*public double exibirValorTotalCarrinho (Integer carrinhoComprasId){

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", securityFilter.getTokenBruto());

        URI uri = UriComponentsBuilder.fromUriString("http://mscarrinhocompras:8083/api/carrinhos/{carrinhoComprasId}")
                .buildAndExpand(carrinhoComprasId)
                .toUri();

        RequestEntity<Object> request = new RequestEntity<>(headers, HttpMethod.GET, uri);

        ResponseEntity<String> response = restTemplate.exchange(request, String.class);



        if (response.getStatusCode() == HttpStatus.OK) {
            try {
                JsonNode produtoJson = objectMapper.readTree(response.getBody());
                double valorTotal = produtoJson.get("valorTotal").asDouble();
                return valorTotal;
            } catch (IOException e) {
                throw new RuntimeException("Erro no metodo verificarDisponibilidadeProdutos");
            }
        }else{
            throw new RuntimeException("Erro no metodo verificarDisponibilidadeProdutos");
        }
    }*/

    public PagamentoDTO realizarPagamento (String cpf, String numero, Date dataValidade, String cvv, Double valor){
        obterLimiteCartaoCredito(cpf);



        Pagamento meuPagamento = pagamentoRepository.findFirstByCpf(cpf).orElse(null);

        if(meuPagamento != null && meuPagamento.getStatusPagamento() != null) {
            System.out.println("Pagamento duplicado!");
            throw new PagamentoDuplicadoException();
        }

        int quantidadeTotalItens=0;
        PagamentoDTO pagamentoDTO = new PagamentoDTO();
        pagamentoDTO.setIdCarrinhoDeCompras(carrinhoComprasId);
        pagamentoDTO.setItensCarrinho(listarItensCarrinho(carrinhoComprasId));
        pagamentoDTO.setValorTotal(exibirValorTotalCarrinho(carrinhoComprasId));
        for (ItemCarrinhoDTO itemCarrinho : pagamentoDTO.getItensCarrinho()) {
            quantidadeTotalItens = quantidadeTotalItens + itemCarrinho.getQuantidade();
        }
        pagamentoDTO.setQuantidadeTotal(quantidadeTotalItens);
        pagamentoDTO.setTipoPagamento(tipoPagamentoEnum);
        pagamentoDTO.setStatusPagamento("Pagamento realizado com sucesso!");
        Pagamento pagamento = toPagamento(pagamentoDTO);

        pagamentoRepository.save(pagamento);
        pagamentoDTO.setId(pagamento.getId());

        return pagamentoDTO;
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


}
