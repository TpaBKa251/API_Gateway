package ru.tpu.hostel.api_gateway.dto_library.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record TimeSlotResponseDto(
        UUID id,
        LocalDateTime startTime,
        LocalDateTime endTime
) {
}
