package br.com.fiap.mslogin.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UsertTest {

    @Test
    public void test_user_entity_mapping() {
        User user = User.builder()
                .id(1L)
                .usuario("testUser")
                .senha("testPassword")
                .build();

        assertNotNull(user);
        assertEquals(1L, user.getId());
        assertEquals("testUser", user.getUsuario());
        assertEquals("testPassword", user.getSenha());
    }

    private void assertNotNull(User user) {
    }
}
