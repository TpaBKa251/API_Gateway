package ru.tpu.hostel.api_gateway.configurtion;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.tpu.hostel.api_gateway.filter.JwtAuthenticationFilter;

import java.net.URI;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserIdGatewayFilter implements GlobalFilter, Ordered {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private static final List<String> UNMODIFIABLE_REQUESTS = List.of(
            "users/get/by/id",
            "users/get/with/roles",
            "users/get/all",
            "bookings/available/timeline",
            "bookings/available/timeslot",
            "balance",
            "role"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        if (!shouldModifyRequest(exchange.getRequest().getURI().getPath())) {
            return chain.filter(exchange);
        }

        return exchange.getPrincipal()
                .cast(Authentication.class) // Получаем аутентификацию
                .map(jwtAuthenticationFilter::getUserIdFromToken) // Извлекаем userId
                .flatMap(userId -> {
                    // Модифицируем URL, добавляя новый PathVariable
                    String newPath = exchange.getRequest().getURI().getPath() + "/" + userId;

                    // Создаем новый запрос с измененным URL
                    URI newUri = URI.create(exchange.getRequest().getURI().toString().replace(exchange.getRequest().getURI().getPath(), newPath));

                    // Создаем новый запрос с измененным URI
                    ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                            .uri(newUri) // Заменяем URI
                            .build();

                    // Создаем новый обмен с измененным запросом
                    ServerWebExchange modifiedExchange = exchange.mutate()
                            .request(modifiedRequest) // Устанавливаем новый запрос
                            .build();

                    // Передаем новый обмен в цепочку фильтров
                    return chain.filter(modifiedExchange);
                })
                .switchIfEmpty(chain.filter(exchange)); // Если нет аутентификации, продолжаем без изменений
    }

    private boolean shouldModifyRequest(String request) {
        for (String unmodifiableRequest : UNMODIFIABLE_REQUESTS) {
            if (request.contains(unmodifiableRequest)) {
                return false;
            }
        }

        return true;
    }


    @Override
    public int getOrder() {
        return -1; // Устанавливаем приоритет фильтра
    }
}


