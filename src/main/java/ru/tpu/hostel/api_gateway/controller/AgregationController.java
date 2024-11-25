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
                .flatMap(authentication -> {

                    String page = request.queryParam("page").orElse("0");
                    String size = request.queryParam("size").orElse("1000000000");
                    String firstName = request.queryParam("firstName").orElse("");
                    String lastName = request.queryParam("lastName").orElse("");
                    String middleName = request.queryParam("middleName").orElse("");
                    String room = request.queryParam("room").orElse("");
                    String negative = request.queryParam("negative").orElse(null);
                    String value = request.queryParam("value").orElse(null);
                    String fluraPast = request.queryParam("fluraPast").orElse(null);
                    String fluraDate = request.queryParam("fluraDate").orElse(null);
                    String certPast = request.queryParam("certPast").orElse(null);
                    String certDate = request.queryParam("certDate").orElse(null);

                    return agregationService.getAllUsers(
                                    authentication,
                                    page,
                                    size,
                                    firstName,
                                    lastName,
                                    middleName,
                                    room,
                                    negative,
                                    value,
                                    fluraPast,
                                    fluraDate,
                                    certPast,
                                    certDate
                            ) // Получение данных из сервиса
                            .collectList() // Преобразование Flux в List
                            .flatMap(users -> ServerResponse.ok().bodyValue(users));
                })
                .switchIfEmpty(ServerResponse.status(401).build()); // Ответ, если Principal отсутствует
    }
}



