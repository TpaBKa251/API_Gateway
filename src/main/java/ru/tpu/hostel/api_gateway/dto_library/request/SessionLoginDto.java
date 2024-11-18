package ru.tpu.hostel.api_gateway.dto_library.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SessionLoginDto(
        @NotBlank(message = "Email не может быть пустым")
        @Email(message = "Email в неверном формате. Пример: example@mail.com")
        String email,

        @NotBlank(message = "Пароль не может быть пустым")
        String password
) {
}
