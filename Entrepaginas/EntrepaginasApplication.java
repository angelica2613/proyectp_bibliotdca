package com.example.Entrepaginas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@SpringBootApplication
public class EntrepaginasApplication {
    public static void main(String[] args) {
        SpringApplication.run(EntrepaginasApplication.class, args);
    }

    @Configuration
    public class WebConfig implements WebMvcConfigurer {
        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            registry.addResourceHandler("/static/**")
                   .addResourceLocations("classpath:/static/");

            registry.addResourceHandler("/css/**")
                   .addResourceLocations("classpath:/static/css/");

            registry.addResourceHandler("/js/**")
                   .addResourceLocations("classpath:/static/js/");
        }
    }
    
}
