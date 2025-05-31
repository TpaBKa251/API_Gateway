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

    public Mono<ServerResponse> getWholeUser(ServerRequest request) {
        return request.principal()
                .cast(Authentication.class)
                .flatMap(authentication -> {
                    String room = request.queryParam("room").orElse("");
                    return agregationService.getWholeUser(authentication, room)
                            .flatMap(response -> ServerResponse.ok().bodyValue(response));

                })
                .switchIfEmpty(ServerResponse.status(502).bodyValue("Сервис не доступен"));
    }

    public Mono<ServerResponse> getAllUsers(ServerRequest request) {
        return request.principal()
                .cast(Authentication.class)
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
                            )
                            .collectList()
                            .flatMap(users -> ServerResponse.ok().bodyValue(users));
                })
                .onErrorResume(throwable -> ServerResponse.status(502).bodyValue("Сервис не доступен"));
    }

    public Mono<ServerResponse> getAllBookingsWithUsers(ServerRequest request) {
        String bookingType = request.pathVariable("type");
        String date = request.pathVariable("date");

        return request.principal()
                .cast(Authentication.class)
                .flatMap(authentication -> agregationService.getAllBookings(bookingType, date)
                        .collectList()
                        .flatMap(list -> ServerResponse.ok().bodyValue(list)));
    }

    public Mono<ServerResponse> getAvailableTimeSlots(ServerRequest request) {
        String bookingType = request.pathVariable("bookingType");
        String date = request.pathVariable("date");

        return request.principal()
                .cast(Authentication.class)
                .flatMap(authentication -> agregationService.getAllAvailableTimeSlots(bookingType, date, authentication)
                        .flatMap(response -> ServerResponse.ok().bodyValue(response)))
                .switchIfEmpty(ServerResponse.status(400).build());
    }

    public Mono<ServerResponse> checkAvailability(ServerRequest request) {
        return ServerResponse.ok().bodyValue("Сервер доступен");
    }
}



