package ru.tpu.hostel.api_gateway.dto;

import java.util.UUID;

public record UserShortResponseWithBookingIdDto(
        String firstName,
        String lastName,
        String middleName,
        UUID bookingId,
        String tgLink,
        String vkLink
) {
}
