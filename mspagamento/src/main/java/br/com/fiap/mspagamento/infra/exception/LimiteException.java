package br.com.fiap.mspagamento.infra.exception;

public class LimiteException  extends RuntimeException {

    public LimiteException(String message) {
        super(message);
    }

}
