package ru.tpu.hostel.api_gateway.configurtion;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import java.util.List;

@Configuration
public class CorsConfig implements WebFluxConfigurer {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowCredentials(true); // Разрешает отправку куки
        corsConfig.addAllowedHeader("*");    // Разрешает все заголовки
        corsConfig.addAllowedMethod("*");    // Разрешает все HTTP-методы
        corsConfig.setAllowedOriginPatterns(List.of("*")); // Динамическое определение источников
        corsConfig.addExposedHeader("Authorization"); // Кастомные заголовки для ответа

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source); // Используем CorsWebFilter для WebFlux
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowCredentials(true) // Разрешает отправку куки
                .allowedHeaders("*")    // Разрешает все заголовки
                .allowedMethods("*")    // Разрешает все HTTP-методы
                .allowedOriginPatterns("*") // Динамическое определение источников
                .exposedHeaders("Authorization"); // Кастомные заголовки для ответа
    }
}





