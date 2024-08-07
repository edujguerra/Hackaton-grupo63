package br.com.fiap.mspagamento.infra.exception;

public class PagamentoDuplicadoException extends RuntimeException {

    public PagamentoDuplicadoException() {
        super("Pagamento já realizado.");
    }

    public PagamentoDuplicadoException(String message) {
        super(message);
    }

    public PagamentoDuplicadoException(String message, Throwable cause) {
        super(message, cause);
    }
}
