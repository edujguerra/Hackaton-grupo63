package br.com.fiap.mspagamento;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MsPagamentoApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	public void test_application_starts_with_no_arguments() {
		String[] args = {};
		MsPagamentoApplication.main(args);
	}

}
