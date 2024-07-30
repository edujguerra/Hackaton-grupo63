package br.com.fiap.mscliente.service;

import br.com.fiap.mscliente.infra.exception.UnauthorizedException;
import br.com.fiap.mscliente.model.CepResponse;
import br.com.fiap.mscliente.model.Cliente;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import br.com.fiap.mscliente.repository.ClienteRepository;
import org.springframework.web.client.RestTemplate;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository repository) {
        this.clienteRepository = repository;
    }

    public ResponseEntity<Object> salvar(Cliente cliente)  {

        try {
            ResponseEntity<Object> response = validaCampos(cliente);
            if (!response.getStatusCode().equals(HttpStatus.OK)  ){
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Cliente com problemas..." + response.getBody());
            }

            cliente = clienteRepository.save(cliente);
            //return ResponseEntity.ok("Id_Cliente : " + cliente.getId());
            return ResponseEntity.ok(cliente);

        } catch (BadCredentialsException e) {
            throw new UnauthorizedException(401, "Usuário e/ou senha inválido(s).");
        }
    }

    private ResponseEntity<Object> validaCampos(Cliente cliente) {

        if (cliente.getNome() == null
                || cliente.getNome().isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Nome não pode ser vazio.");
        }
        if (cliente.getEmail() == null ||
                cliente.getEmail().isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Email não pode ser vazio.");
        }
        if (cliente.getCep() == null ||
                cliente.getCep().isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Cep não pode ser vazio.");
        }
        if (cliente.getCpf() == null ||
                cliente.getCpf().isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("CPF não pode ser vazio.");
        }
        if (cliente.getPais() == null ||
                cliente.getPais().isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Pais não pode ser vazio.");
        }

        try {
            String uriCep = "https://viacep.com.br/ws/" + cliente.getCep() + "/json/";
            RestTemplate restTemplate = new RestTemplate();

            CepResponse cepResponse = restTemplate.getForEntity(uriCep, CepResponse.class).getBody();
            if (cepResponse.getCep() == null ||
                    cepResponse.getCep().isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cep não encontrado. ");
            }

            if (cliente.getRua() == null ||
                    cliente.getRua().isEmpty()) {
                cliente.setRua(cepResponse.getLogradouro());
            }
            if (cliente.getCidade() == null ||
                    cliente.getCidade().isEmpty()) {
                cliente.setCidade(cepResponse.getLocalidade());
            }
            if (cliente.getUf() == null ||
                    cliente.getUf().isEmpty()) {
                cliente.setUf(cepResponse.getUf());
            }
        } catch (Exception err) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cep não encontrado. ");
        }
        return ResponseEntity.ok(cliente);
    }

    public ResponseEntity<Object> buscarUm(Integer id ) {

        Cliente cliente = clienteRepository.findById(id).orElse(null);

        if (cliente != null) {
            return ResponseEntity.ok(cliente);
        } else {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Cliente não encontrado.");
        }
    }

}
