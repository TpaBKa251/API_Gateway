package ru.tpu.hostel.api_gateway.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record AdminResponseDto(
        UUID id,
        String firstName,
        String lastName,
        String middleName,
        String room,
        CertificateDto pediculosis,
        CertificateDto fluorography,
        BigDecimal balance
) {
}
