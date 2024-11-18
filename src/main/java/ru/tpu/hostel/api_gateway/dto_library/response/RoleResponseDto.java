package ru.tpu.hostel.api_gateway.dto_library.response;

import java.util.UUID;

public record RoleResponseDto(
        UUID id,
        UUID user,
        String role
) {
}
