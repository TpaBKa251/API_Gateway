package ru.tpu.hostel.api_gateway.dto;

import ru.tpu.hostel.api_gateway.dto_library.response.DocumentResponseDto;

import java.math.BigDecimal;
import java.util.UUID;

public record AdminResponseDto(
        UUID id,
        String firstName,
        String lastName,
        String middleName,
        String room,
        DocumentResponseDto pediculosis,
        DocumentResponseDto fluorography,
        BigDecimal balance
) {
}
