package ru.tpu.hostel.api_gateway.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record BalanceResponseDto(
        UUID user,
        BigDecimal balance
) {
}
