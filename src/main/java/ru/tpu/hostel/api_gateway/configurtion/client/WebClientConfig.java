package ru.tpu.hostel.api_gateway.configurtion.client;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties(WebClientProperties.class)
@RequiredArgsConstructor
public class WebClientConfig {

    private final WebClientProperties webClientProperties;

    @Bean
    public WebClient adminWebClient(WebClient.Builder builder) {
        return builder.baseUrl(webClientProperties.adminService()).build();
    }

    @Bean
    public WebClient bookingWebClient(WebClient.Builder builder) {
        return builder.baseUrl(webClientProperties.bookingService()).build();
    }

    @Bean
    public WebClient userWebClient(WebClient.Builder builder) {
        return builder.baseUrl(webClientProperties.userService()).build();
    }

    @Bean
    public WebClient scheduleWebClient(WebClient.Builder builder) {
        return builder.baseUrl(webClientProperties.scheduleService()).build();
    }
}

