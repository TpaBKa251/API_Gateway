package ru.tpu.hostel.api_gateway.dto_library.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import ru.tpu.hostel.api_gateway.enums.DocumentType;

import java.time.LocalDate;
import java.util.UUID;

public record DocumentRequestDto(
        @NotNull(message = "Пользователь не может быть пустым")
        UUID user,

        @NotNull(message = "Тип документа не может быть пустым")
        DocumentType type,

        @NotNull(message = "Стартовая дата не может быть пустой")
        LocalDate startDate,

        @NotNull(message = "Конечная дата не может быть пустой")
        @Future(message = "Конечная дата должна быть в будущем")
        LocalDate endDate
) {
}
