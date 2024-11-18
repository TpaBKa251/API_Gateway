package ru.tpu.hostel.api_gateway.dto_library.response;

public record UserShortResponseDto(
        String firstName,
        String lastName,
        String middleName,
        String email,
        String phoneNumber,
        String roomNumber
) {
}
