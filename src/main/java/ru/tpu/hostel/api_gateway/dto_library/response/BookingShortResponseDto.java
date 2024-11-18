package ru.tpu.hostel.api_gateway.dto_library.response;

import java.time.LocalDateTime;

public record BookingShortResponseDto(
        LocalDateTime startTime,
        LocalDateTime endTime
) {
}
