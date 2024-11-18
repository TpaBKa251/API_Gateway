package ru.tpu.hostel.api_gateway.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import ru.tpu.hostel.api_gateway.service.AgregationService;

@Component
@RequiredArgsConstructor
public class AgregationController {

    private final AgregationService agregationService;

    // Обработчик для получения одного пользователя
    public Mono<ServerResponse> getWholeUser(ServerRequest request) {
        return request.principal()
                .cast(Authentication.class) // Приведение Principal к Authentication
                .flatMap(authentication ->
                        agregationService.getWholeUser(authentication) // Получение данных из сервиса
                                .flatMap(response -> ServerResponse.ok().bodyValue(response)) // Формирование успешного ответа
                )
                .switchIfEmpty(ServerResponse.status(401).build()); // Ответ, если Principal отсутствует
    }

    // Обработчик для получения списка пользователей
    public Mono<ServerResponse> getAllUsers(ServerRequest request) {
        return request.principal()
                .cast(Authentication.class) // Приведение Principal к Authentication
                .flatMap(authentication ->
                        agregationService.getAllUsers(authentication) // Получение данных из сервиса
                                .collectList() // Преобразование Flux в List
                                .flatMap(users -> ServerResponse.ok().bodyValue(users)) // Формирование успешного ответа
                )
                .switchIfEmpty(ServerResponse.status(401).build()); // Ответ, если Principal отсутствует
    }
}



