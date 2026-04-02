package com.example.Entrepaginas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {
    @Bean
    public RestTemplate restTemplate() {
        System.out.println("Creando bean RestTemplate"); // Para depurar
        return new RestTemplate();
    }
}