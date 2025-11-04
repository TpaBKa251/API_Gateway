package ru.tpu.hostel.api_gateway.dto;

import java.time.LocalDateTime;
import java.util.List;

public record BookingResponseWithUsersDto(
        LocalDateTime startTime,
        LocalDateTime endTime,
        String type,
        List<UserShortResponseWithBookingIdDto> users
) {
}
