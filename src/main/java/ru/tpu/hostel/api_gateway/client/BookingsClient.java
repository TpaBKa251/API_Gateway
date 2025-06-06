package ru.tpu.hostel.api_gateway.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.tpu.hostel.api_gateway.dto.ActiveEventDto;
import ru.tpu.hostel.api_gateway.dto.ActiveEventWithUserDto;
import ru.tpu.hostel.api_gateway.dto.AvailableTimeSlotsWithResponsible;
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
                .uri(uriBuilder -> uriBuilder.path("bookings/get/all/by/status/user/{status}/{userId}")
                        .build(status, userId))
                .retrieve()
                .bodyToFlux(ActiveEventDto.class);
    }

    public Flux<ActiveEventWithUserDto> getAllByTypeAndDate(String type, String date) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("bookings/get/all/by/type/date/{type}/{date}")
                        .build(type, date))
                .retrieve()
                .bodyToFlux(ActiveEventWithUserDto.class);
    }

    public Flux<ActiveEventWithUserDto> getAllByDate(String date) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("bookings/get/all/by/date/{date}")
                        .build(date))
                .retrieve()
                .bodyToFlux(ActiveEventWithUserDto.class);
    }

    public Mono<AvailableTimeSlotsWithResponsible> getAvailableTimeSlots(String date, String bookingType, UUID userId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("bookings/available/timeslot/{date}/{bookingType}/{userId}")
                        .build(date, bookingType, userId))
                .retrieve()
                .bodyToMono(AvailableTimeSlotsWithResponsible.class);
    }
}

