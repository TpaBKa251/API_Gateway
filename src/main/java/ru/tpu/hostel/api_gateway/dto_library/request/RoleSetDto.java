package ru.tpu.hostel.api_gateway.dto_library.request;

import jakarta.validation.constraints.NotNull;
import ru.tpu.hostel.api_gateway.enums.Roles;

import java.util.UUID;

public record RoleSetDto(
        @NotNull
        UUID user,

        @NotNull
        Roles role
) {
}
