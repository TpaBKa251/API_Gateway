package ru.tpu.hostel.api_gateway.configurtion;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import ru.tpu.hostel.api_gateway.filter.JwtAuthenticationFilter;
import ru.tpu.hostel.api_gateway.filter.JwtService;

import java.util.UUID;

@SuppressWarnings({"NullableProblems", "ReactorTransformationOnMonoVoid"})
@Component
@RequiredArgsConstructor
@Order(-1)
public class UserInfoGatewayFilter implements GlobalFilter {

    private final JwtService jwtService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return exchange.getPrincipal()
                .cast(Authentication.class)
                .flatMap(authentication -> {
                    UUID userId = jwtService.getUserIdFromToken(authentication);
                    String roles = jwtService.getRolesFromToken(authentication);

                    ServerHttpRequestDecorator decorator = new ServerHttpRequestDecorator(exchange.getRequest()) {
                        @Override
                        public HttpHeaders getHeaders() {
                            HttpHeaders headers = new HttpHeaders();
                            headers.putAll(super.getHeaders());
                            headers.add("X-User-Id", userId.toString());
                            headers.add("X-User-Roles", roles);
                            return headers;
                        }
                    };
                    ServerWebExchange decoratedExchange = exchange.mutate()
                            .request(decorator)
                            .build();
                    return chain.filter(decoratedExchange);
                })
                .switchIfEmpty(chain.filter(exchange));
    }

}


