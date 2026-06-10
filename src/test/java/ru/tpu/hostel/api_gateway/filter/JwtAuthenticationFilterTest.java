package ru.tpu.hostel.api_gateway.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.tpu.hostel.api_gateway.TestData;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtAuthenticationFilterTest {

    private JwtAuthenticationFilter filter;

    private WebFilterChain chain;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthenticationFilter(TestData.JWT_SECRET);
        chain = exchange -> Mono.empty();
    }

    @Test
    void filterWhenTokenIsMissing() {
        ServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/get/whole/user"));

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();
    }

    @Test
    void filterWhenAuthHeaderHasNoBearerPrefix() {
        ServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/get/whole/user")
                        .header(HttpHeaders.AUTHORIZATION, TestData.validToken()));

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();
    }

    @Test
    void filterWithValidToken() {
        String token = TestData.validToken(TestData.USER_ID, List.of(TestData.ROLE_STUDENT));
        ServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/get/whole/user")
                        .header(HttpHeaders.AUTHORIZATION, TestData.bearer(token)));

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();
    }

    @Test
    void filterWhenTokenIsExpired() {
        String token = TestData.expiredToken(TestData.USER_ID, List.of(TestData.ROLE_STUDENT));
        ServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/get/whole/user")
                        .header(HttpHeaders.AUTHORIZATION, TestData.bearer(token)));

        assertThatThrownBy(() -> filter.filter(exchange, chain))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(e -> assertThat(((ResponseStatusException) e).getStatusCode())
                        .isEqualTo(HttpStatus.UNAUTHORIZED));
    }

    @Test
    void filterWhenTokenIsMalformed() {
        ServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/get/whole/user")
                        .header(HttpHeaders.AUTHORIZATION, TestData.bearer(TestData.MALFORMED_TOKEN)));

        assertThatThrownBy(() -> filter.filter(exchange, chain))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(e -> assertThat(((ResponseStatusException) e).getStatusCode())
                        .isEqualTo(HttpStatus.UNAUTHORIZED));
    }

    @Test
    void filterWhenSignatureIsWrong() {
        String token = TestData.tokenWithWrongSecret(TestData.USER_ID, List.of(TestData.ROLE_STUDENT));
        ServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/get/whole/user")
                        .header(HttpHeaders.AUTHORIZATION, TestData.bearer(token)));

        assertThatThrownBy(() -> filter.filter(exchange, chain))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void filterWhenUserIdIsMissingInClaims() {
        String token = TestData.tokenWithoutUserId();
        ServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/get/whole/user")
                        .header(HttpHeaders.AUTHORIZATION, TestData.bearer(token)));

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();
    }

    @Test
    void filterWhenRolesAreMissingInClaims() {
        String token = TestData.tokenWithoutRoles();
        ServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/get/whole/user")
                        .header(HttpHeaders.AUTHORIZATION, TestData.bearer(token)));

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();
    }
}
