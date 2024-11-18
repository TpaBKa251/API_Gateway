package ru.tpu.hostel.api_gateway.dto_library.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record BookingTimeSlotRequestDto(
        @NotNull(message = "Номер слота не может быть пустым")
        UUID slotId
) {
}
