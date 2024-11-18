package ru.tpu.hostel.api_gateway.dto_library.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record SuperUserResponseDto(
        UUID id,
        String firstName,
        String lastName,
        String middleName,
        String roomNumber,
        List<String> role,
        BigDecimal balance,
        CertificateDto certificateFluorography,
        CertificateDto certificatePediculosis,
        List<ActiveEventDto> activeEvents
) {}

