package br.com.fiap.mscliente.infra.exception;

public class UnauthorizedException extends RuntimeException  {

    private final String message;

    public UnauthorizedException(String message) {
        this.message = message;
    }

}
