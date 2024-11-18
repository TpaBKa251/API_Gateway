package ru.tpu.hostel.api_gateway.dto;


import ru.tpu.hostel.api_gateway.enums.DocumentType;

import java.time.LocalDate;
import java.util.UUID;

public record CertificateDto(
        UUID id,
        UUID user,
        DocumentType type,
        LocalDate startDate,
        LocalDate endDate
) {}

