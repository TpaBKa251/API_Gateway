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
import ru.tpu.hostel.api_gateway.enums.BookingStatus;

class BookingsClientTest {

    private static final String OBJECT_BODY = "{}";

    private static final String ARRAY_BODY = "[{}]";

    private static final String TYPE_GYM = "GYM";

    private static final String DATE = "2026-06-09";

    private BookingsClient bookingsClient(String body) {
        ExchangeFunction exchangeFunction = request -> Mono.just(ClientResponse.create(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(body)
                .build());
        WebClient webClient = WebClient.builder()
                .baseUrl("http://localhost")
                .exchangeFunction(exchangeFunction)
                .build();
        return new BookingsClient(webClient);
    }

    @Test
    void getAllByStatusWithSuccess() {
        StepVerifier.create(bookingsClient(ARRAY_BODY).getAllByStatus(BookingStatus.BOOKED, TestData.USER_ID))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void getAllByTypeAndDateWithSuccess() {
        StepVerifier.create(bookingsClient(ARRAY_BODY).getAllByTypeAndDate(TYPE_GYM, DATE))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void getAllByDateWithSuccess() {
        StepVerifier.create(bookingsClient(ARRAY_BODY).getAllByDate(DATE))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void getAvailableTimeSlotsWithSuccess() {
        StepVerifier.create(bookingsClient(OBJECT_BODY).getAvailableTimeSlots(DATE, TYPE_GYM, TestData.USER_ID))
                .expectNextCount(1)
                .verifyComplete();
    }
}
