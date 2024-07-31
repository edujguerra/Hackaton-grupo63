package br.com.fiap.mslogin.model;

import br.com.fiap.mslogin.entity.User;
import br.com.fiap.mslogin.repository.UserRepository;
import br.com.fiap.mslogin.service.CustomUserDetailsService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class CustomUserDetailsTest {

    @Mock
    private UserRepository userRepository;

    @Test
    public void test_initialization() {
        CustomUserDetailsTest customUserDetailsTest = new CustomUserDetailsTest();
        assertNotNull(customUserDetailsTest);
    }

    @Test
    public void test_constructor_initializes_username_correctly() {
        User user = new User();
        user.setUsuario("testUser");
        user.setSenha("testPassword");

        CustomUserDetails customUserDetails = new CustomUserDetails(user);

        assertEquals("testUser", customUserDetails.getUsername());
    }

    @Test
    public void test_returns_null_when_called() {
        User user = new User();
        user.setUsuario("testUser");
        user.setSenha("testPassword");
        CustomUserDetails userDetails = new CustomUserDetails(user);
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        assertNull(authorities);
    }

    @Test
    public void test_returns_correct_password() {
        User user = new User();
        user.setUsuario("testUser");
        user.setSenha("testPassword");
        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        assertEquals("testPassword", customUserDetails.getPassword());
    }

    @Test
    public void test_method_always_returns_true() {
        User user = new User();
        user.setUsuario("testUser");
        user.setSenha("testPassword");
        CustomUserDetails userDetails = new CustomUserDetails(user);
        assertTrue(userDetails.isAccountNonExpired());
    }

    @Test
    public void test_is_account_non_locked_for_any_user() {
        User user = new User();
        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        assertTrue(customUserDetails.isAccountNonLocked());
    }

    @Test
    public void test_valid_usuario_and_senha_create_user_successfully() {
        SingUpRequest request = new SingUpRequest("validUser", "validPassword");
        User user = request.toUser();

        assertNotNull(user);
        assertEquals("validUser", user.getUsuario());
        assertEquals("validPassword", user.getSenha());
    }

    @Test
    public void test_convert_user_to_userdto_successfully() {
        User user = User.builder()
                .id(1L)
                .usuario("testUser")
                .senha("password")
                .build();
        UserDTO userDTO = UserDTO.fromUser(user);
        assertEquals(user.getId(), userDTO.id());
        assertEquals(user.getUsuario(), userDTO.usuario());
    }

    @Test
    public void converts_user_to_userdto_successfully() {
        User user = User.builder()
                .id(1L)
                .usuario("testUser")
                .senha("password")
                .build();
        UserDTO userDTO = UserDTO.fromUser(user);
        assertEquals(1L, userDTO.id());
        assertEquals("testUser", userDTO.usuario());
    }

}
