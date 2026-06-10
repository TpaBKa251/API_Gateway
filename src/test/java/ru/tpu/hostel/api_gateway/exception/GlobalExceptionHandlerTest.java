package ru.tpu.hostel.api_gateway.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.test.StepVerifier;

import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleServiceExceptionWithClientError() {
        ServiceException ex = new ServiceException.BadRequest("Неверный запрос");

        StepVerifier.create(handler.handleServiceException(ex))
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(response.getBody())
                            .containsEntry("code", "400")
                            .containsEntry("message", "Неверный запрос");
                })
                .verifyComplete();
    }

    @Test
    void handleServiceExceptionWithServerError() {
        ServiceException ex = new ServiceException.BadGateway("Шлюз недоступен");

        StepVerifier.create(handler.handleServiceException(ex))
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
                    assertThat(response.getBody()).containsEntry("message", "Шлюз недоступен");
                })
                .verifyComplete();
    }

    @Test
    void handleServiceExceptionWhenCauseIsPresent() {
        ServiceException ex = new ServiceException.NotFound("исходное сообщение", new RuntimeException("причина"));

        StepVerifier.create(handler.handleServiceException(ex))
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(response.getBody()).containsEntry("message", "причина");
                })
                .verifyComplete();
    }

    @Test
    void handleConnectionExceptionWithKnownLocalPort() {
        ConnectException ex = new ConnectException("Connection refused: localhost:8081");

        StepVerifier.create(handler.handleConnectionException(ex))
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
                    Map<String, String> body = response.getBody();
                    assertThat(body).isNotNull();
                    assertThat(body.get("message")).contains("Сервис пользователей");
                })
                .verifyComplete();
    }

    @Test
    void handleConnectionExceptionWithKubernetesName() {
        UnknownHostException ex = new UnknownHostException("Failed to resolve 'userservice'") {
            @Override
            public String getMessage() {
                throw new RuntimeException("no message");
            }
        };

        StepVerifier.create(handler.handleConnectionException(ex))
                .assertNext(response ->
                        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY))
                .verifyComplete();
    }

    @Test
    void handleConnectionExceptionWhenPortDoesNotMatchKnownService() {
        ConnectException ex = new ConnectException("Connection refused: localhost:9999");

        StepVerifier.create(handler.handleConnectionException(ex))
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
                    assertThat(response.getBody().get("message")).contains("Сервис");
                })
                .verifyComplete();
    }

    @Test
    void handleExceptionReturnsInternalServerError() {
        Exception ex = new IllegalStateException("неожиданная ошибка");

        StepVerifier.create(handler.handleException(ex))
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
                    assertThat(response.getBody()).containsEntry("message", "неожиданная ошибка");
                })
                .verifyComplete();
    }
}
