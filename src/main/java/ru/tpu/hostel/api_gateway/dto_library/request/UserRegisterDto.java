package ru.tpu.hostel.api_gateway.dto_library.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserRegisterDto(
        @Size(message = "Имя должно быть от 1 до 50 символов", min = 1, max = 50)
        @NotBlank(message = "Имя не может быть пустым")
        @Pattern(regexp = "^[А-ЯЁ][а-яё]{1,50}$",
                message = "Имя должно быть написано буквами русского алфавита и начинаться с заглавной буквы")
        String firstName,

        @Size(message = "Фамилия должна быть от 1 до 50 символов", min = 1, max = 50)
        @NotBlank(message = "Фамилия не может быть пустой")
        @Pattern(regexp = "^[А-ЯЁ][а-яё]{1,50}$",
                message = "Фамилия должна быть написана буквами русского алфавита и начинаться с заглавной буквы")
        String lastName,

        @Size(max = 50, message = "Отчество должно быть не более 50 символов")
        @Pattern(regexp = "^$|^([А-ЯЁ][а-яё]{0,50})$",
                message = "Отчество должно быть написано буквами русского алфавита и начинаться с заглавной буквы")
        String middleName,

        @Size(min = 5, max = 100, message = "Email должен быть от 5 до 100 символов")
        @NotBlank(message = "Email не может быть пустым")
        @Email(message = "Неверный формат email. Пример: example@mail.com")
        String email,

        String phoneNumber,

        String password,

        @NotBlank
        String roomNumber
) {
}
