package ru.tpu.hostel.api_gateway.configurtion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
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
public class UserRoleGatewayFilter implements GlobalFilter, Ordered {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private static final List<String> MODIFIABLE_REQUESTS = List.of(
            "roles"
    );


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!shouldModifyRequest(exchange.getRequest().getURI().getPath())) {
            return chain.filter(exchange);
        }

        log.info("Я пошел модифицировать роли");

        return exchange.getPrincipal()
                .cast(Authentication.class)
                .map(jwtAuthenticationFilter::getRolesFromToken)
                .flatMap(role ->{
                    String newPath = exchange.getRequest().getURI().getPath() + "/" + role;
                    URI newUri = URI.create(exchange.getRequest().getURI().toString().replace(exchange.getRequest().getURI().getPath(), newPath));

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
        for (String modifiableRequest : MODIFIABLE_REQUESTS) {
            if (request.contains(modifiableRequest)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
