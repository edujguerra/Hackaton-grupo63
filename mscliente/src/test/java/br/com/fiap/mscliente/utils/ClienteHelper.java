package br.com.fiap.mscliente.utils;

import br.com.fiap.mscliente.model.Cliente;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class ClienteHelper {
    public static Cliente gerarCliente() {
        Cliente cliente = new Cliente();
        cliente.setNome("Eduardo");
        cliente.setCpf("10212");
        cliente.setCep("95020-190");
        cliente.setEmail("email");
        cliente.setPais("pais");
        cliente.setTelefone("1234566");

        return cliente;
    }

    public static String asJsonString(final Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
    }
}
