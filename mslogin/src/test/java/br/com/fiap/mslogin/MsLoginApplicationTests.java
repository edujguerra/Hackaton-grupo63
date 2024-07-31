package br.com.fiap.mslogin;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MsLoginApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    public void test_application_starts_with_no_arguments() {
        String[] args = {};
        MsLoginApplication.main(args);
    }
}
