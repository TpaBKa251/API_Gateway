package ru.tpu.hostel.api_gateway.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import ru.tpu.hostel.api_gateway.dto.ActiveEventDto;

import java.util.UUID;

@Component
public class SchedulesClient {

    private final WebClient webClient;

    public SchedulesClient(@Qualifier("scheduleWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public Flux<ActiveEventDto> getActiveKitchenSchedules(UUID userId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/schedules/kitchen/get/on/room/{userId}")
                        .build(userId))
                .retrieve()
                .bodyToFlux(ActiveEventDto.class);
    }
}
