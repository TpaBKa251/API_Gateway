package ru.tpu.hostel.api_gateway.dto;

import java.util.UUID;

public record TimeSlotResponseDto(
        UUID id,
        String time
) {
}
