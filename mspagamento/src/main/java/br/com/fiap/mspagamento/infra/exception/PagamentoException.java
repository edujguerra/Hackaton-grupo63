package br.com.fiap.mspagamento.infra.exception;

public class PagamentoException  extends RuntimeException {


    public PagamentoException() {
        super("Ocorreu um erro com o pagamento.");
    }

    public PagamentoException(String message) {
        super(message);
    }

    public PagamentoException(String message, Throwable cause) {
        super(message, cause);
    }
}
