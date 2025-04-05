package ru.tpu.hostel.api_gateway.configurtion;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;
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
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeExchange(auth -> auth
                        .pathMatchers(HttpMethod.POST, "/api/get/all/users").hasRole("ADMINISTRATION")
                        //.pathMatchers(HttpMethod.GET, "/api/get/all/users").hasRole("ADMINISTRATION")
                        .pathMatchers(HttpMethod.POST, "/balance").hasRole("ADMINISTRATION")
                        .pathMatchers(HttpMethod.PATCH, "/balance/edit").hasRole("ADMINISTRATION")
                        .pathMatchers(HttpMethod.PATCH, "/balance/edit/adding").hasRole("ADMINISTRATION")
                        .pathMatchers(HttpMethod.GET, "/balance/get/all").hasRole("ADMINISTRATION")
                        .pathMatchers(HttpMethod.GET, "/balance/get/short/").hasRole("ADMINISTRATION")
                        .pathMatchers(HttpMethod.POST, "/documents").hasRole("ADMINISTRATION")
                        .pathMatchers(HttpMethod.PATCH, "/documents/edit").hasRole("ADMINISTRATION")
                        .pathMatchers(HttpMethod.GET, "/documents/get/all").hasRole("ADMINISTRATION")
                        .pathMatchers(HttpMethod.GET, "/users/get/all").hasRole("ADMINISTRATION")
                        .pathMatchers(HttpMethod.GET, "api/bookings/get/all/{type}/{date}").access((authentication, context) -> {
                            String bookingType = context.getVariables().get("type").toString();

                            List<String> roles;
                            switch (bookingType.toUpperCase()) {
                                case "GYM" -> roles = List.of(
                                        "ROLE_ADMINISTRATION",
                                        "ROLE_HOSTEL_SUPERVISOR",
                                        "ROLE_MAIN_RESPONSIBLE_GYM"
                                );
                                case "HALL" -> roles = List.of(
                                        "ROLE_ADMINISTRATION",
                                        "ROLE_HOSTEL_SUPERVISOR",
                                        "ROLE_RESPONSIBLE_HALL"
                                );
                                case "INTERNET" -> roles = List.of(
                                        "ROLE_ADMINISTRATION",
                                        "ROLE_HOSTEL_SUPERVISOR",
                                        "ROLE_RESPONSIBLE_INTERNET"
                                );
                                case "ALL" -> roles = List.of("ROLE_ADMINISTRATION", "ROLE_HOSTEL_SUPERVISOR");
                                default -> {
                                    return Mono.just(new AuthorizationDecision(false));
                                }
                            }

                            return authentication
                                    .map(auth1 -> auth1.isAuthenticated() &&
                                            auth1.getAuthorities().stream()
                                                    .anyMatch(grantedAuthority ->
                                                            roles.contains(grantedAuthority.getAuthority())))
                                    .map(AuthorizationDecision::new);
                        })
                        .pathMatchers(HttpMethod.POST, "/responsibles").access((authentication, context) -> {
                            String responsibleType = context.getVariables().get("type").toString();

                            List<String> roles;
                            switch (responsibleType.toUpperCase()) {
                                case "GYM" -> roles = List.of(
                                        "ROLE_ADMINISTRATION",
                                        "ROLE_HOSTEL_SUPERVISOR",
                                        "ROLE_MAIN_RESPONSIBLE_GYM"
                                );
                                case "HALL" -> roles = List.of(
                                        "ROLE_ADMINISTRATION",
                                        "ROLE_HOSTEL_SUPERVISOR",
                                        "ROLE_RESPONSIBLE_HALL"
                                );
                                case "INTERNET" -> roles = List.of(
                                        "ROLE_ADMINISTRATION",
                                        "ROLE_HOSTEL_SUPERVISOR",
                                        "ROLE_RESPONSIBLE_INTERNET"
                                );
                                default -> {
                                    return Mono.just(new AuthorizationDecision(false));
                                }
                            }

                            return authentication
                                    .map(authentication1 -> authentication1.isAuthenticated() &&
                                            authentication1.getAuthorities().stream()
                                                    .anyMatch(grantedAuthority ->
                                                            roles.contains(grantedAuthority.getAuthority())))
                                    .map(AuthorizationDecision::new);
                        })
                        .pathMatchers(HttpMethod.POST, "/users").permitAll()
                        .pathMatchers(HttpMethod.POST, "/sessions").permitAll()
                        .pathMatchers(HttpMethod.GET, "/sessions/auth/token").permitAll()
                        .pathMatchers(HttpMethod.POST, "/sessions/auth/token").permitAll()
                        .pathMatchers(HttpMethod.OPTIONS, "/api").permitAll()
                        .pathMatchers("/actuator/**").permitAll()
                        .pathMatchers("/grafana**").permitAll()
                        .pathMatchers("/grafana/**").permitAll()
                        .pathMatchers("/login**").permitAll()
                        .pathMatchers("/login/**").permitAll()
                        .pathMatchers("/error/**").permitAll()
                        .pathMatchers("/error**").permitAll()
                        .anyExchange().authenticated()
                )
                .addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .build();
    }

    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowCredentials(true);
        corsConfig.addAllowedHeader("*");
        corsConfig.addAllowedMethod("*");
        corsConfig.setAllowedOriginPatterns(List.of("*"));
        corsConfig.addExposedHeader("Authorization");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return source;
    }
}



