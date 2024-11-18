package ru.tpu.hostel.api_gateway.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.tpu.hostel.api_gateway.dto.UserResponseDto;
import ru.tpu.hostel.api_gateway.dto.UserResponseWithRoleDto;

import java.util.UUID;

@Component
public class UserClient {


    private final WebClient webClient;

    public UserClient(@Qualifier("userWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<UserResponseWithRoleDto> getUserWithRoles(UUID id) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("users/get/with/roles/{id}").build(id))
                .retrieve()
                .bodyToMono(UserResponseWithRoleDto.class);
    }

    public Flux<UserResponseDto> getAllUsers() {
        return webClient.get()
                .uri("users/get/all")
                .retrieve()
                .bodyToFlux(UserResponseDto.class);
    }
}

