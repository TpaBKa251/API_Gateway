package ru.tpu.hostel.api_gateway.handler;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.tpu.hostel.api_gateway.exception.ServiceUnavailableException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<Map<String, String>> handleServiceUnavailableException(ServiceUnavailableException e) {
        Map<String, String> body = new HashMap<>();

        body.put("code", "401");
        body.put("message", e.getMessage());


        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }
}
