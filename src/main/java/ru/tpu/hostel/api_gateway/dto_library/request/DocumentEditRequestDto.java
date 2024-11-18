package ru.tpu.hostel.api_gateway.dto_library.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record DocumentEditRequestDto(
        @NotNull(message = "Номер документа не может быть пустым")
        UUID id,

        @NotNull(message = "Стартовая дата не может быть пустой")
        LocalDate startDate,

        @NotNull(message = "Конечная дата не может быть пустой")
        @Future(message = "Конечная дата должна быть в будущем")
        LocalDate endDate
) {
}
