package ru.tpu.hostel.api_gateway.dto;

import java.util.List;
import java.util.UUID;

public record AvailableTimeSlotsWithResponsible(
        UUID responsibleId,
        List<TimeSlotResponseDto> timeSlots
) {
}
