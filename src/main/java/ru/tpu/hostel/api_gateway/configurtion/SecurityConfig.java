package ru.tpu.hostel.api_gateway.configurtion;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.tpu.hostel.api_gateway.enums.Roles;
import ru.tpu.hostel.api_gateway.filter.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "api/get/all/users").hasRole("ADMINISTRATION")
                        .requestMatchers(HttpMethod.POST, "balance").hasRole("ADMINISTRATION")
                        .requestMatchers(HttpMethod.PATCH, "balance/edit").hasRole("ADMINISTRATION")
                        .requestMatchers(HttpMethod.PATCH, "balance/edit/adding").hasRole("ADMINISTRATION")
                        .requestMatchers(HttpMethod.GET, "balance/get/all").hasRole("ADMINISTRATION")
                        .requestMatchers(HttpMethod.GET, "balance/get/short/").hasRole("ADMINISTRATION")
                        .requestMatchers(HttpMethod.POST, "documents").hasRole("ADMINISTRATION")
                        .requestMatchers(HttpMethod.PATCH, "documents/edit").hasRole("ADMINISTRATION")
                        .requestMatchers(HttpMethod.GET, "documents/get/all").hasRole("ADMINISTRATION")
                        .requestMatchers(HttpMethod.GET, "users/get/all").hasRole("ADMINISTRATION")
                        .requestMatchers(HttpMethod.POST, "users").permitAll()
                        .requestMatchers(HttpMethod.POST, "sessions").permitAll()
                        .requestMatchers(HttpMethod.POST, "sessions/auth/token").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }
}


