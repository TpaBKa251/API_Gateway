package ru.tpu.hostel.api_gateway.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

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

    public List<String> getListRolesFromToken(Authentication authentication) {
        return authentication.getAuthorities()
                .stream()
                .map(role -> {
                    String roleStr = role.toString();
                    roleStr = roleStr.replace("ROLE_", "");
                    return roleStr;
                })
                .toList();
    }

    private Key getSigningKey() {
        return new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS512.getJcaName());
    }

}
