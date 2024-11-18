package ru.tpu.hostel.api_gateway.service;

import org.springframework.security.core.Authentication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.tpu.hostel.api_gateway.dto.AdminResponseDto;
import ru.tpu.hostel.api_gateway.dto.WholeUserResponseDto;

import java.util.List;

public interface AgregationService {

    Mono<WholeUserResponseDto> getWholeUser(Authentication authentication);

    Flux<List<AdminResponseDto>> getAllUsers(Authentication authentication);
}
