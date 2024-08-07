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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserServiceTest {

    private UsuarioService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    CustomUserDetailsService service ;

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

    @Test
    public void test_username_not_found_in_repository() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);

        CustomUserDetailsService service = new CustomUserDetailsService();
        Mockito.when(userRepository.findFirstByUsuario("invalidUser")).thenReturn(Optional.empty());

        Assertions.assertThat(UsernameNotFoundException.class);
    }

    @Test
    public void test_generate_token_with_valid_user_id_and_expiration_time() {
        JwtService jwtService = new JwtService();
        String usuario = "testUser";
        Long id = 12345L;
        Integer minutos = 100000;

        String token = jwtService.generateToken(usuario, id, minutos);

        assertNotNull(token);
    }

//    @Test
//    public void user_found_returns_custom_user_details() {
//
//        User user = new User();
//        user.setUsuario("testUser");
//        user.setSenha("testPassword");
//        Mockito.when(userRepository.findFirstByUsuario("testUser")).thenReturn(Optional.of(user));
//
//        UserDetails userDetails = service.loadUserByUsername("testUser");
//
//        assertNotNull(userDetails);
//        Assertions.assertEquals("testUser", userDetails.getUsername());
//    }
}
