package ru.tpu.hostel.api_gateway.configurtion;

//@Configuration
//public class FeignConfig {
//
//    @Value("${jwt.secret}")
//    private String secretKey;
//
//    @Bean
//    public RequestInterceptor requestInterceptor() {
//        return requestTemplate -> {
//            // Извлечение данных из SecurityContext
//            SecurityContext context = SecurityContextHolder.getContext();
//            Authentication authentication = context.getAuthentication();
//
//            if (authentication != null && authentication.getCredentials() instanceof String token) {
//                Claims claims = getClaimsFromToken(token);
//
//                requestTemplate.header("X-User-Id", claims.get("userId", String.class));
//                requestTemplate.header("X-Roles", String.join(",", claims.get("roles", List.class)));
//            }
//        };
//    }
//
//    private Claims getClaimsFromToken(String token) {
//        // Создание объекта Key из секретного ключа
//        Key signingKey = Keys.hmacShaKeyFor(secretKey.getBytes());
//
//        // Парсинг токена
//        return Jwts.parserBuilder()
//                .setSigningKey(signingKey) // Теперь передаем Key
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//    }
//}
