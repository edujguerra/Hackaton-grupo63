package br.com.fiap.mspagamento.controller;

import br.com.fiap.mspagamento.infra.exception.LimiteException;
import br.com.fiap.mspagamento.infra.exception.PagamentoDuplicadoException;
import br.com.fiap.mspagamento.infra.exception.PagamentoException;
import br.com.fiap.mspagamento.model.entity.Pagamento;
import br.com.fiap.mspagamento.model.response.RegistrarPagamentoResponse;
import br.com.fiap.mspagamento.service.PagamentoService;
import jakarta.validation.Valid;

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

    public PagamentoController() {
        //TODO Auto-generated constructor stub
    }

    @PostMapping
    public ResponseEntity<?> registrarPagamento (@Valid @RequestBody Pagamento pagamento) {
        try{
            //todo gerar uma chave de pegamento unica e nao um sequencial como ID, algo unico como um UUID
            RegistrarPagamentoResponse novoPagamento = pagamentoService.realizarPagamento(pagamento);
            return new ResponseEntity<>(novoPagamento, HttpStatus.OK);
        } catch(PagamentoException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch(LimiteException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.PAYMENT_REQUIRED);
        } catch(NoSuchElementException e) {
            return new ResponseEntity<>("Exception do registrar pagamento", HttpStatus.BAD_REQUEST);
        } catch (PagamentoDuplicadoException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }catch (Exception e) {
            return new ResponseEntity<>("Erro interno de aplicação", HttpStatus.INTERNAL_SERVER_ERROR);
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
        try{
            return new ResponseEntity<>(pagamentoService.obterPagamentosPorCPF(cpf), HttpStatus.OK);
        } catch(PagamentoException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }catch (Exception e) {
            return new ResponseEntity<>("Erro interno de aplicação", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
