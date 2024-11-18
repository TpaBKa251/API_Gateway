package ru.tpu.hostel.api_gateway.dto_library.response;



import ru.tpu.hostel.api_gateway.enums.BookingStatus;
import ru.tpu.hostel.api_gateway.enums.BookingType;

import java.time.LocalDateTime;
import java.util.UUID;

public record BookingResponseDto(
        UUID id,
        LocalDateTime startTime,
        LocalDateTime endTime,
        BookingStatus status,
        BookingType type
) {
}
