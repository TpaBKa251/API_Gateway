package ru.tpu.hostel.api_gateway.client;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.tpu.hostel.api_gateway.dto.UserResponseDto;
import ru.tpu.hostel.api_gateway.dto.UserResponseWithRoleDto;
import ru.tpu.hostel.api_gateway.dto_library.request.SessionLoginDto;
import ru.tpu.hostel.api_gateway.dto_library.request.UserRegisterDto;
import ru.tpu.hostel.api_gateway.dto_library.response.SessionRefreshResponse;
import ru.tpu.hostel.api_gateway.dto_library.response.SessionResponseDto;
import ru.tpu.hostel.api_gateway.dto_library.response.UserShortResponseDto;

import java.util.List;
import java.util.UUID;

@Component
@FeignClient(name = "user-userservice", url = "http://userservice:8080")
public interface UserClient {

    @PostMapping("/users")
    UserResponseDto registerUser(
            @RequestBody UserRegisterDto userRegisterDto
    );

    @GetMapping("/users/profile/{id}")
    UserResponseDto getUser(
            @PathVariable UUID id
    );

    @GetMapping("/users/get/by/id/{id}")
    UserShortResponseDto getUserById(
            @PathVariable UUID id
    );

    @GetMapping("/users/get/with/roles/{id}")
    UserResponseWithRoleDto getUserWithRoles(
            @PathVariable UUID id
    );

    @GetMapping("/users/get/all")
    List<UserResponseDto> getAllUsers();

    @PostMapping("/sessions")
    SessionResponseDto login(
            @RequestBody SessionLoginDto sessionLoginDto
    );

    @PatchMapping("/sessions/logout/{sessionId}/{userId}")
    ResponseEntity<?> logout(
            @PathVariable UUID sessionId,
            @PathVariable UUID userId
    );

    @GetMapping("/sessions/auth/token")
    SessionRefreshResponse refreshToken(
    );
}


