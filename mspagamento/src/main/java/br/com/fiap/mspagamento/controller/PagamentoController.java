package br.com.fiap.mspagamento.controller;

import br.com.fiap.mspagamento.infra.exception.PagamentoDuplicadoException;
import br.com.fiap.mspagamento.model.Pagamento;
import br.com.fiap.mspagamento.service.PagamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;


@RestController
@RequestMapping("/api/pagamentos")
public class PagamentoController {

    @Autowired
    private PagamentoService pagamentoService;

    public PagamentoController(PagamentoService pagamentoService) {
        this.pagamentoService = pagamentoService;
    }

    @PostMapping
    public ResponseEntity<?> registrarPagamento (@RequestBody Pagamento pagamento) {
        try{
            //todo gerar uma chave de pegamento unica e nao um sequencial como ID, algo unico como um UUID
            Pagamento novoPagamento = pagamentoService.realizarPagamento(pagamento);
            return new ResponseEntity<>("chave_pagamento:"+novoPagamento.getId(), HttpStatus.CREATED);
        } catch(NoSuchElementException e) {
            return new ResponseEntity<>("Exception do registrar pagamento", HttpStatus.BAD_REQUEST);
        } catch (PagamentoDuplicadoException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public List<Pagamento> listarPagamentos() {
        return pagamentoService.listarPagamentos();
    }

    @GetMapping("/{pagamentoId}")
    public Pagamento obterPagamentoPorId(@PathVariable Integer pagamentoId) {
        return pagamentoService.obterPagamentoPorId(pagamentoId);
    }

    @GetMapping("/cliente/{cpf}")
    public ResponseEntity<?> obterPagamentosPorCPF(@PathVariable String cpf) {
        return new ResponseEntity<>(pagamentoService.obterPagamentosPorCPF(cpf), HttpStatus.OK);
    }

}
