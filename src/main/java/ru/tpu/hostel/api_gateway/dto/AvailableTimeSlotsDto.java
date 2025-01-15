package ru.tpu.hostel.api_gateway.dto;

import java.util.List;

public record AvailableTimeSlotsDto(
        String responsibleFirstName,
        String responsibleLastName,
        String responsibleMiddleName,
        List<TimeSlotResponseDto> timeSlotResponseDtos
) {
}
