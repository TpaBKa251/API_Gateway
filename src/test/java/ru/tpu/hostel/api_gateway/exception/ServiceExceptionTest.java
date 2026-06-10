package ru.tpu.hostel.api_gateway.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class ServiceExceptionTest {

    @Test
    void serviceExceptionWithMessageAndStatus() {
        ServiceException ex = new ServiceException("сообщение", HttpStatus.BAD_REQUEST);

        assertThat(ex.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(ex.getMessage()).isEqualTo("сообщение");
    }

    @Test
    void serviceExceptionWithStatusOnly() {
        ServiceException ex = new ServiceException(HttpStatus.NOT_FOUND);

        assertThat(ex.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(ex.getMessage()).isEqualTo(HttpStatus.NOT_FOUND.getReasonPhrase());
    }

    @Test
    void serviceExceptionWithMessageStatusAndCause() {
        RuntimeException cause = new RuntimeException("причина");
        ServiceException ex = new ServiceException("сообщение", HttpStatus.CONFLICT, cause);

        assertThat(ex.getStatus()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(ex.getCause()).isEqualTo(cause);
    }

    @Test
    void serviceExceptionWrappingAnotherServiceException() {
        ServiceException original = new ServiceException.Forbidden("запрещено");
        ServiceException ex = new ServiceException("обертка", original);

        assertThat(ex.getStatus()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(ex.getCause()).isEqualTo(original);
    }

    @Test
    void badRequestDefaultMessage() {
        ServiceException ex = new ServiceException.BadRequest();

        assertThat(ex.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(ex.getMessage()).isEqualTo("Bad Request");
    }

    @Test
    void unauthorizedWithCause() {
        RuntimeException cause = new RuntimeException("c");
        ServiceException ex = new ServiceException.Unauthorized("нет доступа", cause);

        assertThat(ex.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(ex.getCause()).isEqualTo(cause);
    }

    @Test
    void notFoundDefaultMessage() {
        assertThat(new ServiceException.NotFound().getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void badGatewayWithMessage() {
        ServiceException ex = new ServiceException.BadGateway("шлюз");

        assertThat(ex.getStatus()).isEqualTo(HttpStatus.BAD_GATEWAY);
        assertThat(ex.getMessage()).isEqualTo("шлюз");
    }

    @Test
    void internalServerErrorDefaultMessage() {
        assertThat(new ServiceException.InternalServerError().getStatus())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void serviceUnavailableWithCause() {
        ServiceException ex = new ServiceException.ServiceUnavailable("недоступен", new RuntimeException());

        assertThat(ex.getStatus()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
    }

    @Test
    void conflictDefaultMessage() {
        assertThat(new ServiceException.Conflict().getStatus()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void serviceUnavailableExceptionMessage() {
        ServiceUnavailableException ex = new ServiceUnavailableException("сервис недоступен");

        assertThat(ex.getMessage()).isEqualTo("сервис недоступен");
    }
}
