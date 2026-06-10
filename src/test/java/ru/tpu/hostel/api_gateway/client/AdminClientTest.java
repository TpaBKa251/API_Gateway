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
import ru.tpu.hostel.api_gateway.enums.DocumentType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

class AdminClientTest {

    private static final String OBJECT_BODY = "{}";

    private static final String ARRAY_BODY = "[{}]";

    private AdminClient adminClient(String body) {
        ExchangeFunction exchangeFunction = request -> Mono.just(ClientResponse.create(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(body)
                .build());
        WebClient webClient = WebClient.builder()
                .baseUrl("http://localhost")
                .exchangeFunction(exchangeFunction)
                .build();
        return new AdminClient(webClient);
    }

    @Test
    void getBalanceShortWithSuccess() {
        StepVerifier.create(adminClient(OBJECT_BODY).getBalanceShort(TestData.USER_ID))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void getAllBalancesWithSuccess() {
        StepVerifier.create(adminClient(ARRAY_BODY).getAllBalances(0, 10, false, BigDecimal.TEN))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void getDocumentByTypeWithSuccess() {
        StepVerifier.create(adminClient(OBJECT_BODY).getDocumentByType(TestData.USER_ID, DocumentType.FLUOROGRAPHY))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void getAllDocumentsWithSuccess() {
        StepVerifier.create(adminClient(ARRAY_BODY)
                        .getAllDocuments(0, 10, false, LocalDate.now(), false, LocalDate.now()))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void getAllDocumentsByUsersWithSuccess() {
        StepVerifier.create(adminClient(ARRAY_BODY).getAllDocumentsByUsers(List.of(TestData.USER_ID)))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void getAllBalancesByUsersWithSuccess() {
        StepVerifier.create(adminClient(ARRAY_BODY).getAllBalancesByUsers(List.of(TestData.USER_ID)))
                .expectNextCount(1)
                .verifyComplete();
    }
}
