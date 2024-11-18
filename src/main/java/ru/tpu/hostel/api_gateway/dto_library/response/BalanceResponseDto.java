package ru.tpu.hostel.api_gateway.dto_library.response;

import java.math.BigDecimal;
import java.util.UUID;

public record BalanceResponseDto(
        UUID user,
        BigDecimal balance
) {
}
