package br.com.fiap.mslogin.config;


import br.com.fiap.mslogin.service.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AuthConfigTest {

    @Test
    public void test_userDetailsService_bean_instantiation() {
        AuthConfig authConfig = new AuthConfig();
        UserDetailsService userDetailsService = authConfig.userDetailsService();
        assertNotNull(userDetailsService);
        assertTrue(userDetailsService instanceof CustomUserDetailsService);
    }
}
