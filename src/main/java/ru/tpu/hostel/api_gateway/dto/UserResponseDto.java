package ru.tpu.hostel.api_gateway.dto;

import java.util.UUID;

public record UserResponseDto(
        UUID id,
        String firstName,
        String lastName,
        String middleName,
        String email,
        String phoneNumber,
        String roomNumber
) {
}
