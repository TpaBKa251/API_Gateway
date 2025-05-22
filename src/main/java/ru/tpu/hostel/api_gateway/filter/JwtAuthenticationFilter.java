package ru.tpu.hostel.api_gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.ThreadContext;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@SuppressWarnings("NullableProblems")
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {

    private final String secretKey;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String method = exchange.getRequest().getMethod().name();
        String path = exchange.getRequest().getURI().getPath();
        log.info("[REQUEST] Получен запрос: {} {}", method, path);

        String token = extractToken(exchange.getRequest().getHeaders());

        if (token == null) {
            log.warn("Токен пустой");
            final long startTime = System.currentTimeMillis();
            return chain.filter(exchange).doFinally(signalType ->
                    logResponse(exchange, signalType, startTime));
        }

        if (!validateToken(token)) {
            log.error("Токен истек или недействителен");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Токен истек или недействителен");
        }

        Claims claims = getClaimsFromToken(token);

        return Mono.justOrEmpty(createAuthentication(claims))
                .flatMap(authentication -> {
                    SecurityContext context = new SecurityContextImpl(authentication);
                    final long startTime = System.currentTimeMillis();
                    return chain.filter(exchange)
                            .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)))
                            .doFinally(signalType -> logResponse(exchange, signalType, startTime));
                });
    }

    private String extractToken(HttpHeaders headers) {
        String authHeader = headers.getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.substring(7);
    }

    private boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.error("Invalid token: {}", e.getMessage());
            return false;
        }
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Authentication createAuthentication(Claims claims) {
        if (claims == null) {
            return null;
        }

        String userId = claims.get("userId", String.class);
        List<String> roles = claims.get("roles", List.class);

        if (userId == null || roles == null) {
            log.error("User ID or roles are null");
            return null;
        }

        List<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());

        return new UsernamePasswordAuthenticationToken(userId, claims, authorities);
    }

    private void logResponse(ServerWebExchange exchange, SignalType signalType, long startTime) {
        long elapsedTime = System.currentTimeMillis() - startTime;
        int statusCode = Objects.requireNonNull(exchange.getResponse().getStatusCode()).value();
        String logMsg = switch (signalType) {
            case ON_COMPLETE -> "[RESPONSE] Запрос успешно выполнен, статус {}. Время выполнения: {} мс";
            case ON_ERROR -> "[RESPONSE] Запрос выполнен с ошибкой, статус {}. Время выполнения: {} мс";
            case CANCEL -> "[RESPONSE] Выполнение запроса закрыто, статус {}. Время выполнения: {} мс";
            default -> "[RESPONSE] Выполнение запроса завершено: " + signalType
                    + ", статус {}. Время выполнения: {} мс";
        };
        log.info(logMsg, statusCode, elapsedTime);
    }

}


