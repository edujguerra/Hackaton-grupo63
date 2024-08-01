package br.com.fiap.controller;

import br.com.fiap.model.CartaoCredito;
import br.com.fiap.model.CartaoCreditoDTO;
import br.com.fiap.service.CartaoCreditoService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class ControllerTest {

    @Test
    public void test_gerar_cartao_credito_valid_dto() {
        CartaoCreditoService cartaoCreditoService = Mockito.mock(CartaoCreditoService.class);
        CartaoCreditoController controller = new CartaoCreditoController(cartaoCreditoService);
        ReflectionTestUtils.setField(controller, "cartaoCreditoService", cartaoCreditoService);

        CartaoCreditoDTO dto = new CartaoCreditoDTO(1L, "12345678900", 5000.0, "1234567890123456", new Date(), "123");
        CartaoCredito cartaoCredito = new CartaoCredito(1L, "12345678900", 5000.0, "1234567890123456", new Date(), "123");

        when(cartaoCreditoService.gerarCartaoCredito(dto)).thenReturn(cartaoCredito);

        ResponseEntity<?> response = controller.gerarCartaoCredito(dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(cartaoCredito, response.getBody());
    }

}
