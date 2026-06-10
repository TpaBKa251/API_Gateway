package ru.tpu.hostel.api_gateway.controller;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import ru.tpu.hostel.api_gateway.service.AgregationService;

import static org.mockito.Mockito.mock;

class AgregationRouterTest {

    @Test
    void agregationRoutesCheckAvailability() {
        AgregationController controller = new AgregationController(mock(AgregationService.class));
        RouterFunction<ServerResponse> routes = new AgregationRouter().agregationRoutes(controller);
        WebTestClient client = WebTestClient.bindToRouterFunction(routes).build();

        client.options()
                .uri("/api")
                .exchange()
                .expectStatus().isOk();
    }
}
