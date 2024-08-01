package br.com.fiap.config;

import org.junit.jupiter.api.Test;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigTest {

    @Test
    public void test_rest_template_bean_creation_success() {
        AppConfig appConfig = new AppConfig();
        RestTemplate restTemplate = appConfig.restTemplate();
        assertNotNull(restTemplate);
    }

    @Test
    public void test_single_grouped_openapi() {
        SwaggerConfig swaggerConfig = new SwaggerConfig();
        List<GroupedOpenApi> apis = swaggerConfig.apis();
        assertNotNull(apis);
        assertEquals(1, apis.size());
        assertEquals("cartaocredito_api", apis.get(0).getGroup());
    }
}
