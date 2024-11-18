package ru.tpu.hostel.api_gateway.dto_library.response;

import java.time.LocalDateTime;

public record AvailableTimeBookingDto(
        LocalDateTime startTime,
        LocalDateTime endTime
) {
}
