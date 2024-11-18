package ru.tpu.hostel.api_gateway.dto_library.response;


import ru.tpu.hostel.api_gateway.enums.DocumentType;

import java.time.LocalDate;

public record DocumentShortResponseDto(
        DocumentType type,
        LocalDate startDate,
        LocalDate endDate
) {
}
