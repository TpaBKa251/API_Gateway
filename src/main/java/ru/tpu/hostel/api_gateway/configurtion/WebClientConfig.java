package ru.tpu.hostel.api_gateway.configurtion;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    @Qualifier("adminWebClient")
    public WebClient adminWebClient(WebClient.Builder builder) {
        return builder.baseUrl("http://administrationservice:8080").build();
    }

    @Bean
    @Qualifier("bookingWebClient")
    public WebClient bookingWebClient(WebClient.Builder builder) {
        return builder.baseUrl("http://bookingservice:8080").build();
    }

    @Bean
    @Qualifier("userWebClient")
    public WebClient userWebClient(WebClient.Builder builder) {
        return builder.baseUrl("http://userservice:8080").build();
    }
}

