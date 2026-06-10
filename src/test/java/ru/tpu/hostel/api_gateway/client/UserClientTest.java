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

import java.util.List;

class UserClientTest {

    private static final String OBJECT_BODY = "{}";

    private static final String ARRAY_BODY = "[{}]";

    private UserClient userClient(String body) {
        ExchangeFunction exchangeFunction = request -> Mono.just(ClientResponse.create(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(body)
                .build());
        WebClient webClient = WebClient.builder()
                .baseUrl("http://localhost")
                .exchangeFunction(exchangeFunction)
                .build();
        return new UserClient(webClient);
    }

    @Test
    void getUserWithRolesWithSuccess() {
        StepVerifier.create(userClient(OBJECT_BODY).getUserWithRoles(TestData.USER_ID))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void getAllUsersWithSuccess() {
        StepVerifier.create(userClient(ARRAY_BODY).getAllUsers(0, 10, "", "", "", ""))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void getAllUsersWithIdsWithSuccess() {
        StepVerifier.create(userClient(ARRAY_BODY).getAllUsersWithIds(List.of(TestData.USER_ID)))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void getAllUsersWithIdsShortWithSuccess() {
        StepVerifier.create(userClient(ARRAY_BODY).getAllUsersWithIdsShort(List.of(TestData.USER_ID)))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void getUserWithIdShortWithSuccess() {
        StepVerifier.create(userClient(OBJECT_BODY).getUserWithIdShort(TestData.USER_ID))
                .expectNextCount(1)
                .verifyComplete();
    }
}
