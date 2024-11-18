package ru.tpu.hostel.api_gateway.dto;

import ru.tpu.hostel.api_gateway.dto_library.response.BookingResponseDto;
import ru.tpu.hostel.api_gateway.dto_library.response.DocumentResponseDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record WholeUserResponseDto(
        UUID id,
        String firstName,
        String lastName,
        String middleName,
        String roomNumber,
        List<String> role,
        BigDecimal balance,
        DocumentResponseDto fluorography,
        DocumentResponseDto pediculosis,
        List<BookingResponseDto> activeEvents
) {
}
