package ru.tpu.hostel.api_gateway.controller;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.test.StepVerifier;
import ru.tpu.hostel.api_gateway.service.AgregationService;

import static org.mockito.Mockito.mock;

class AgregationControllerTest {

    private final AgregationService agregationService = mock(AgregationService.class);

    private final AgregationController agregationController = new AgregationController(agregationService);

    @Test
    void checkAvailabilityWithSuccess() {
        StepVerifier.create(agregationController.checkAvailability(null))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void getWholeUserWhenPrincipalIsEmpty() {
        RouterFunction<ServerResponse> routes = new AgregationRouter().agregationRoutes(agregationController);
        WebTestClient client = WebTestClient.bindToRouterFunction(routes).build();

        client.get()
                .uri("/api/get/whole/user")
                .exchange()
                .expectStatus().isEqualTo(502);
    }
}
