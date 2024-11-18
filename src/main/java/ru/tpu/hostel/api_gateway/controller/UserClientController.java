package ru.tpu.hostel.api_gateway.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.tpu.hostel.api_gateway.client.UserClient;
import ru.tpu.hostel.api_gateway.dto.UserResponseDto;
import ru.tpu.hostel.api_gateway.dto.UserResponseWithRoleDto;
import ru.tpu.hostel.api_gateway.dto_library.request.SessionLoginDto;
import ru.tpu.hostel.api_gateway.dto_library.request.UserRegisterDto;
import ru.tpu.hostel.api_gateway.dto_library.response.SessionRefreshResponse;
import ru.tpu.hostel.api_gateway.dto_library.response.SessionResponseDto;
import ru.tpu.hostel.api_gateway.dto_library.response.UserShortResponseDto;
import ru.tpu.hostel.api_gateway.filter.JwtAuthenticationFilter;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping()
public class UserClientController {

    private final UserClient userClient;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // Регистрация пользователя
    @PostMapping("users")
    public UserResponseDto registerUser(
            @RequestBody @Valid UserRegisterDto userRegisterDto
    ) {
        return userClient.registerUser(userRegisterDto);
    }

    // Получение профиля текущего пользователя
    @GetMapping("users/profile")
    public UserResponseDto getProfile(Authentication authentication) {
        UUID userId = jwtAuthenticationFilter.getUserIdFromToken(authentication);
        return userClient.getUser(userId);
    }

    // Получение информации о пользователе по ID
    @GetMapping("users/get/by/id/{id}")
    public UserShortResponseDto getUserById(
            @PathVariable UUID id
    ) {
        return userClient.getUserById(id);
    }

    // Получение пользователя с ролями
    @GetMapping("users/get/with/roles/{id}")
    public UserResponseWithRoleDto getUserWithRoles(
            @PathVariable UUID id
    ) {
        return userClient.getUserWithRoles(id);
    }

    // Получение всех пользователей
    @GetMapping("users/get/all")
    public List<UserResponseDto> getAllUsers() {
        return userClient.getAllUsers();
    }

    // Авторизация пользователя
    @PostMapping("/sessions")
    public SessionResponseDto login(
            @RequestBody @Valid SessionLoginDto sessionLoginDto,
            HttpServletResponse response
    ) {
        return userClient.login(sessionLoginDto);
    }

    // Выход из системы
    @PatchMapping("/sessions/logout")
    public ResponseEntity<?> logout(
            @RequestParam UUID sessionId,
            Authentication authentication,
            HttpServletResponse response
    ) {
        UUID userId = jwtAuthenticationFilter.getUserIdFromToken(authentication);
        return userClient.logout(sessionId, userId);
    }

    // Обновление токена
    @GetMapping("/sessions/auth/token")
    public SessionRefreshResponse refreshToken(
            @CookieValue("refreshToken") String refreshToken,
            HttpServletResponse response
    ) {
        return userClient.refreshToken();
    }
}

