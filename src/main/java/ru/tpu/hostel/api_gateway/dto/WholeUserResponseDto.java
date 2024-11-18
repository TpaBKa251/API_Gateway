package ru.tpu.hostel.api_gateway.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record WholeUserResponseDto(
        UUID id,
        String firstName,
        String lastName,
        String middleName,
        String roomNumber,
        List<String> role,
        BigDecimal balance,
        CertificateDto fluorography,
        CertificateDto pediculosis,
        List<ActiveEventDto> activeEvents
) {
}
