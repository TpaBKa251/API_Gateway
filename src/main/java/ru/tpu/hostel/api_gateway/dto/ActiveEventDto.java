package ru.tpu.hostel.api_gateway.dto;


import ru.tpu.hostel.api_gateway.enums.BookingStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record ActiveEventDto(
        UUID id,
        LocalDateTime startTime,
        LocalDateTime endTime,
        BookingStatus status,
        String type
) {}