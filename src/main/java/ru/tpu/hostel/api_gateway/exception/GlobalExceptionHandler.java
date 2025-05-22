package ru.tpu.hostel.api_gateway.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import reactor.core.publisher.Mono;
import ru.tpu.hostel.api_gateway.enums.Microservice;

import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    public Mono<ResponseEntity<Map<String, String>>> handleServiceException(ServiceException ex) {
        if (ex.getStatus().value() >= 500 && ex.getStatus().value() < 600) {
            log.error(ex.getMessage(), ex);
        }
        if (ex.getCause() != null) {
            return getMonoResponseEntity(ex.getStatus(), ex.getCause().getMessage());
        }
        return getMonoResponseEntity(ex.getStatus(), ex.getMessage());
    }

    @ExceptionHandler({ConnectException.class, UnknownHostException.class})
    public Mono<ResponseEntity<Map<String, String>>> handleConnectionException(Exception ex) {
        String unavailableMicroserviceName;
        try {
            String localPort = ex.getMessage().substring(ex.getMessage().lastIndexOf(":") + 1);
            unavailableMicroserviceName = Microservice.getMicroserviceNameLocal(localPort);
        } catch (Exception e1) {
            try {
                String kubernetesName = ex.getMessage().substring(
                        ex.getMessage().indexOf("'") + 1,
                        ex.getMessage().lastIndexOf("'")
                );
                unavailableMicroserviceName = Microservice.getMicroserviceNameInKubernetes(kubernetesName);
            } catch (Exception e2) {
                unavailableMicroserviceName = "Сервис";
            }
        }

        String errorMessage = unavailableMicroserviceName + " временно не доступен. Повторите попытку позже";
        return getMonoResponseEntity(HttpStatus.BAD_GATEWAY, errorMessage);
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<Map<String, String>>> handleException(Exception ex) {
        log.error(ex.getMessage(), ex);
        return getMonoResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    private Mono<ResponseEntity<Map<String, String>>> getMonoResponseEntity(HttpStatus status, String message) {
        Map<String, String> map = new HashMap<>();
        map.put("code", String.valueOf(status.value()));
        map.put("message", message);

        return Mono.just(ResponseEntity.status(status).body(map));
    }
}
