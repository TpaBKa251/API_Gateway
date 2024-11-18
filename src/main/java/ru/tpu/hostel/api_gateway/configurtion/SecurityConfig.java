package ru.tpu.hostel.api_gateway.configurtion;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import ru.tpu.hostel.api_gateway.filter.JwtAuthenticationFilter;

import java.util.List;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable) // Отключение CSRF
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Подключаем CORS из существующей конфигурации
                .authorizeExchange(auth -> auth
                        .pathMatchers(HttpMethod.GET, "/api/get/all/users").hasRole("ADMINISTRATION")
                        .pathMatchers(HttpMethod.POST, "/balance").hasRole("ADMINISTRATION")
                        .pathMatchers(HttpMethod.PATCH, "/balance/edit").hasRole("ADMINISTRATION")
                        .pathMatchers(HttpMethod.PATCH, "/balance/edit/adding").hasRole("ADMINISTRATION")
                        .pathMatchers(HttpMethod.GET, "/balance/get/all").hasRole("ADMINISTRATION")
                        .pathMatchers(HttpMethod.GET, "/balance/get/short/").hasRole("ADMINISTRATION")
                        .pathMatchers(HttpMethod.POST, "/documents").hasRole("ADMINISTRATION")
                        .pathMatchers(HttpMethod.PATCH, "/documents/edit").hasRole("ADMINISTRATION")
                        .pathMatchers(HttpMethod.GET, "/documents/get/all").hasRole("ADMINISTRATION")
                        .pathMatchers(HttpMethod.GET, "/users/get/all").hasRole("ADMINISTRATION")
                        .pathMatchers(HttpMethod.POST, "/users").permitAll()
                        .pathMatchers(HttpMethod.POST, "/sessions").permitAll()
                        .pathMatchers(HttpMethod.POST, "/sessions/auth/token").permitAll()
                        .anyExchange().authenticated() // Для всех остальных маршрутов требуется аутентификация
                )
                .addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION) // Подключение JWT-фильтра
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance()) // Отключение сохранения контекста
                .build();
    }

    // Метод для получения CORS-конфигурации
    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowCredentials(true); // Разрешение отправки куки
        corsConfig.addAllowedHeader("*");    // Разрешение всех заголовков
        corsConfig.addAllowedMethod("*");    // Разрешение всех методов
        corsConfig.setAllowedOriginPatterns(List.of("*")); // Разрешение всех источников
        corsConfig.addExposedHeader("Authorization"); // Кастомные заголовки для ответа

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return source;
    }
}



