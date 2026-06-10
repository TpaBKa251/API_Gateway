package ru.tpu.hostel.api_gateway.client;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.tpu.hostel.api_gateway.TestData;

class SchedulesClientTest {

    private static final String ARRAY_BODY = "[{}]";

    private SchedulesClient schedulesClient() {
        ExchangeFunction exchangeFunction = request -> Mono.just(ClientResponse.create(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(ARRAY_BODY)
                .build());
        WebClient webClient = WebClient.builder()
                .baseUrl("http://localhost")
                .exchangeFunction(exchangeFunction)
                .build();
        return new SchedulesClient(webClient);
    }

    @Test
    void getActiveKitchenSchedulesWithSuccess() {
        StepVerifier.create(schedulesClient().getActiveKitchenSchedules(TestData.USER_ID.toString()))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void getActiveResponsiblesWithSuccess() {
        StepVerifier.create(schedulesClient().getActiveResponsibles(TestData.USER_ID))
                .expectNextCount(1)
                .verifyComplete();
    }
}
