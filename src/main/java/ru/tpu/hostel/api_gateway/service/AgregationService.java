package ru.tpu.hostel.api_gateway.service;

import org.springframework.security.core.Authentication;
import ru.tpu.hostel.api_gateway.dto.AdminResponseDto;
import ru.tpu.hostel.api_gateway.dto.WholeUserResponseDto;

import java.util.List;

public interface AgregationService {

    WholeUserResponseDto getWholeUser(Authentication authentication);

    List<AdminResponseDto> getAllUsers(Authentication authentication);
}
