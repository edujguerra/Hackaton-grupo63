package br.com.fiap.mspagamento.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class ConfigTest {
    // ObjectMapper bean is created successfully
    @Test
    public void test_object_mapper_creation_success() {
        JacksonConfig config = new JacksonConfig();
        ObjectMapper mapper = config.objectMapper();
        assertNotNull(mapper);
        assertFalse(mapper.getRegisteredModuleIds().contains(JavaTimeModule.class.getName()));
    }

    // RestTemplate bean is created successfully
    @Test
    public void rest_template_bean_creation() {
        AppConfig appConfig = new AppConfig();
        RestTemplate restTemplate = appConfig.restTemplate();
        assertNotNull(restTemplate);
        assertTrue(restTemplate.getMessageConverters().stream()
                .anyMatch(converter -> converter instanceof MappingJackson2HttpMessageConverter));
    }
}
