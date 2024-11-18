package ru.tpu.hostel.api_gateway.dto_library.request;

import java.util.UUID;

public record SessionRefreshDto(
        UUID userId
) {
}
