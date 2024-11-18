package ru.tpu.hostel.api_gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Value("${jwt.secret}")
    private String secretKey;

    private final static List<String> PERMITTED_ENDPOINTS = List.of(
            "/users",
            "/sessions",
            "/sessions/auth/token"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String endpoint = request.getRequestURI();

        if (PERMITTED_ENDPOINTS.contains(endpoint)) {
            return;
        }

        String token = extractToken(request);

        if (!validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Claims claims = getClaimsFromToken(token);

        // Устанавливаем аутентификацию в SecurityContext
        setAuthentication(claims);

        // Добавляем данные в заголовки запроса
        MutableHttpServletRequest mutableRequest = new MutableHttpServletRequest(request);
        mutableRequest.addHeader("X-User-Id", claims.get("userId", String.class));

        filterChain.doFilter(mutableRequest, response);
    }

    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
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

    private void setAuthentication(Claims claims) {
        if (claims == null) {
            return;
        }

        String userId = claims.get("userId", String.class);
        List<String> roles = claims.get("roles", List.class);

        if (userId == null || roles == null) {
            log.error("User ID or roles are null");
            return;
        }

        List<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());

        Authentication authentication = new UsernamePasswordAuthenticationToken(userId, claims, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
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

    private Key getSigningKey() {
        return new SecretKeySpec(secretKey.getBytes(), SignatureAlgorithm.HS512.getJcaName());
    }
}

