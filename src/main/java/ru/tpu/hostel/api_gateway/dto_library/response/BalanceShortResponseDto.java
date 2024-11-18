package ru.tpu.hostel.api_gateway.dto_library.response;

import java.math.BigDecimal;

public record BalanceShortResponseDto(
        BigDecimal balance
) {
}
