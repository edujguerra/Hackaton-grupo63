package br.com.fiap.mscliente;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@OpenAPIDefinition
@SpringBootApplication
public class MsClienteApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(MsClienteApplication.class, args);
    }

    @Override
    public void run(String... args)  {

    }
}
