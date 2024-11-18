package ru.tpu.hostel.api_gateway.dto_library.response;

import java.math.BigDecimal;
import java.util.UUID;

public record AdminUserResponse(
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
