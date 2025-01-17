package ru.tpu.hostel.api_gateway.dto;

import ru.tpu.hostel.api_gateway.enums.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record BookingResponseWithUsersDto(
        LocalDateTime startTime,
        LocalDateTime endTime,
        String type,
        List<UserShortResponseWithBookingIdDto> users
) {
}
