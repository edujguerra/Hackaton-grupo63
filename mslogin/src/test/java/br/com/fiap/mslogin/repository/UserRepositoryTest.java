package br.com.fiap.mslogin.repository;

import br.com.fiap.mslogin.entity.User;
import br.com.fiap.mslogin.utils.UserHelper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserRepositoryTest {

    @Mock
    private UserRepository userRepository;

    AutoCloseable openMocks;

    @BeforeEach
    void setup() {
        openMocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Test
    void RegistrarUser(){

        // Arrange
        User user = UserHelper.gerarUser();
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        User userArmazenado = userRepository.save(user);

        // Assert
        verify(userRepository, times(1)).save(user);
        assertThat(userArmazenado)
                .isInstanceOf(User.class)
                .isNotNull()
                .isEqualTo(user);
        assertThat(userArmazenado)
                .extracting(User::getId)
                .isEqualTo(user.getId());
    }

    @Test
    void ListarUser(){

        // Arrange
        User user1 = UserHelper.gerarUser();
        User user2 = UserHelper.gerarUser();
        List<User> userList = Arrays.asList(user1, user2);

        when(userRepository.findAll()).thenReturn(userList);

        // Act
        List<User> resultado = userRepository.findAll();

        // Assert
        verify(userRepository, times(1)).findAll();
        Assertions.assertThat(resultado)
                .hasSize(2)
                .containsExactlyInAnyOrder(user1, user2);
    }

    @Test
    void ListarUmUser(){
        // Arrange
        User user = UserHelper.gerarUser();
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        User userArmazenado = userRepository.save(user);

        // Assert
        verify(userRepository, times(1)).save(user);
        Assertions.assertThat(userArmazenado)
                .isInstanceOf(User.class)
                .isNotNull()
                .isEqualTo(user);
        Assertions.assertThat(userArmazenado)
                .extracting(User::getId)
                .isEqualTo(user.getId());
    }
}
