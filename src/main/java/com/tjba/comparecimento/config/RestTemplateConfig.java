package com.tjba.comparecimento.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Configuração do RestTemplate para requisições HTTP.
 */
@Configuration
public class RestTemplateConfig {

    /**
     * Bean do RestTemplate com configurações de timeout
     */
    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();

        // Timeout de conexão (5 segundos)
        factory.setConnectTimeout(5000);

        // Timeout de leitura (10 segundos)
        factory.setReadTimeout(10000);

        RestTemplate restTemplate = new RestTemplate(factory);
        return restTemplate;
    }
}