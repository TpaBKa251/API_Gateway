package ru.tpu.hostel.api_gateway.configurtion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class UserIdGatewayFilter implements GlobalFilter, Ordered {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private static final List<String> UNMODIFIABLE_REQUESTS = List.of(
            "users/get/by/id",
            "users/get/with/roles",
            "users/get/all",
            "bookings/available/timeline",
            //"bookings/available/timeslot",
            "balance",
            "documents/edit",
            "users/get/by/name",
            "responsibles",
            "users/get/by/role"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        if (!shouldModifyRequest(exchange.getRequest().getURI().getPath())) {
            return chain.filter(exchange);
        }

        log.info("Я пошел модифицировать юзера");

        return exchange.getPrincipal()
                .cast(Authentication.class)
                .map(jwtAuthenticationFilter::getUserIdFromToken)
                .flatMap(userId -> {
                    String newPath = exchange.getRequest().getURI().getPath() + "/" + userId;

                    URI newUri = URI.create(exchange.getRequest().getURI().toString()
                            .replace(exchange.getRequest().getURI().getPath(), newPath));

                    ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                            .uri(newUri)
                            .build();

                    ServerWebExchange modifiedExchange = exchange.mutate()
                            .request(modifiedRequest)
                            .build();

                    return chain.filter(modifiedExchange);
                })
                .switchIfEmpty(chain.filter(exchange));
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
        return -1;
    }
}


