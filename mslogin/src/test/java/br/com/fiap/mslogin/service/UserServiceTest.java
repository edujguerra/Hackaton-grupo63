package br.com.fiap.mslogin.service;

import br.com.fiap.mslogin.entity.User;
import br.com.fiap.mslogin.repository.UserRepository;
import br.com.fiap.mslogin.utils.UserHelper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

class UserServiceTest {

    private UsuarioService userService;

    @Mock
    private UserRepository userRepository;

    AutoCloseable openMocks;

    @BeforeEach
    void setup() {

        openMocks = MockitoAnnotations.openMocks(this);
        userService= new UsuarioService(userRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Test
    void ListarUmUser(){

        // Arrange
        Long id = 100L;
        User user = UserHelper.gerarUser();
        user.setId(id);
        Mockito.when(userRepository.findById(ArgumentMatchers.any(Long.class)))
                .thenReturn(Optional.of(user));

        // Act
        ResponseEntity<?> resposta = userService.buscarUm(id);
        User userArmazenado = (User) resposta.getBody();

        // Assert
        Assertions.assertThat(userArmazenado)
                .isInstanceOf(User.class)
                .isNotNull()
                .isEqualTo(user);
        Assertions.assertThat(userArmazenado)
                .extracting(User::getId)
                .isEqualTo(user.getId());
    }

}
