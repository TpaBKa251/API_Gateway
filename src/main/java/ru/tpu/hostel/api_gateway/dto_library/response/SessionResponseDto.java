package ru.tpu.hostel.api_gateway.dto_library.response;

import java.util.UUID;

public record SessionResponseDto(
        UUID id,
        String accessToken
) {
}
