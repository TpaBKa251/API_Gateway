package ru.tpu.hostel.api_gateway.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import ru.tpu.hostel.api_gateway.dto.ActiveEventDto;
import ru.tpu.hostel.api_gateway.enums.BookingStatus;

import java.util.UUID;

@Component
public class BookingsClient {


    private final WebClient webClient;

    public BookingsClient(@Qualifier("bookingWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public Flux<ActiveEventDto> getAllByStatus(BookingStatus status, UUID userId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("bookings/get/all/{status}/{userId}")
                        .build(status, userId))
                .retrieve()
                .bodyToFlux(ActiveEventDto.class);
    }
}

