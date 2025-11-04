package ru.tpu.hostel.api_gateway.dto;

import java.util.UUID;

public record UserShortResponseDto2(
        UUID id,
        String firstName,
        String lastName,
        String middleName,
        String tgLink,
        String vkLink
) {
}
