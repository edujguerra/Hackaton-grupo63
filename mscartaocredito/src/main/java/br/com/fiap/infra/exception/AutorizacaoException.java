package br.com.fiap.infra.exception;

public class AutorizacaoException extends RuntimeException {

    private String message;

    public AutorizacaoException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
