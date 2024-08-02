package br.com.fiap.mspagamento.infra.exception;

public class LimiteException  extends RuntimeException {


    public LimiteException() {
        super("Ocorreu um erro com o pagamento.");
    }

    public LimiteException(String message) {
        super(message);
    }

    public LimiteException(String message, Throwable cause) {
        super(message, cause);
    }
}
