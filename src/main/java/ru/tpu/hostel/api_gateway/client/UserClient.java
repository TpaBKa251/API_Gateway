package ru.tpu.hostel.api_gateway.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.tpu.hostel.api_gateway.dto.UserResponseDto;
import ru.tpu.hostel.api_gateway.dto.UserResponseWithRoleDto;

import java.util.List;
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

    public Flux<UserResponseDto> getAllUsers(
            Integer page,
            Integer size,
            String firstName,
            String lastName,
            String middleName,
            String room
    ) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("users/get/all")
                        .queryParam("page", page != null ? page : 0)
                        .queryParam("size", size != null ? size : 1000000000)
                        .queryParam("firstName", firstName != null ? firstName : "")
                        .queryParam("lastName", lastName != null ? lastName : "")
                        .queryParam("middleName", middleName != null ? middleName : "")
                        .queryParam("room", room != null ? room : "")
                        .build())
                .retrieve()
                .bodyToFlux(UserResponseDto.class);
    }

    public Flux<UserResponseDto> getAllUsersWithIds(List<UUID> ids) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("users/get/all/by/ids")
                        .queryParam("ids", ids.toArray())
                        .build())
                .retrieve()
                .bodyToFlux(UserResponseDto.class);
    }

}

