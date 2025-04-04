package ru.tpu.hostel.api_gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import ru.tpu.hostel.api_gateway.exception.ServiceUnavailableException;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter implements WebFilter {

    @Value("${jwt.secret}")
    private String secretKey;

    private static final List<String> PERMITTED_ENDPOINTS = List.of(
            "/users",
            "/sessions",
            "/sessions/auth/token",
            "/api",
            "/actuator/health",
            "/actuator",
            "/grafana"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String endpoint = exchange.getRequest().getURI().getPath();

        if (PERMITTED_ENDPOINTS.contains(endpoint) || endpoint.contains("actuator") || endpoint.contains("grafana")) {
            return chain.filter(exchange);
        }

        String token = extractToken(exchange.getRequest().getHeaders());

        if (token == null || !validateToken(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Токен истек или недействителен");
        }

        Claims claims = getClaimsFromToken(token);

        return Mono.justOrEmpty(createAuthentication(claims))
                .flatMap(authentication -> {
                    SecurityContext context = new SecurityContextImpl(authentication);
                    return chain.filter(exchange)
                            .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)));
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

    public UUID getUserIdFromToken(Authentication authentication) {
        return UUID.fromString(authentication.getPrincipal().toString());
    }

    public UUID getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return UUID.fromString(claims.get("userId", String.class));
    }

    public String getRolesFromToken(Authentication authentication) {
        return authentication.getAuthorities()
                .stream()
                .map(role -> {
                    String roleStr = role.toString();
                    roleStr = roleStr.replace("ROLE_", "");
                    return roleStr;
                })
                .collect(Collectors.joining(","));
    }

    private Key getSigningKey() {
        return new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS512.getJcaName());
    }
}


