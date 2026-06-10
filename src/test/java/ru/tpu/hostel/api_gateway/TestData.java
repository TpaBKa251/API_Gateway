package ru.tpu.hostel.api_gateway;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import ru.tpu.hostel.api_gateway.dto.ActiveEventDto;
import ru.tpu.hostel.api_gateway.dto.ActiveEventDtoResponse;
import ru.tpu.hostel.api_gateway.dto.ActiveEventWithUserDto;
import ru.tpu.hostel.api_gateway.dto.AvailableTimeSlotsWithResponsible;
import ru.tpu.hostel.api_gateway.dto.BalanceResponseDto;
import ru.tpu.hostel.api_gateway.dto.CertificateDto;
import ru.tpu.hostel.api_gateway.dto.TimeSlotResponseDto;
import ru.tpu.hostel.api_gateway.dto.UserResponseDto;
import ru.tpu.hostel.api_gateway.dto.UserResponseWithRoleDto;
import ru.tpu.hostel.api_gateway.dto.UserShortResponseDto2;
import ru.tpu.hostel.api_gateway.enums.BookingStatus;
import ru.tpu.hostel.api_gateway.enums.DocumentType;

import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class TestData {

    private TestData() {
    }

    public static final String JWT_SECRET =
            "test-secret-key-for-hs512-signature-must-be-at-least-64-bytes-long-1234567890";

    public static final String WRONG_JWT_SECRET =
            "another-wrong-secret-key-for-hs512-must-also-be-at-least-64-bytes-long-0987";

    public static final UUID USER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    public static final UUID OTHER_USER_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");

    public static final UUID RESPONSIBLE_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");

    public static final UUID BOOKING_ID = UUID.fromString("44444444-4444-4444-4444-444444444444");

    public static final UUID TIME_SLOT_ID = UUID.fromString("55555555-5555-5555-5555-555555555555");

    public static final UUID DOCUMENT_ID = UUID.fromString("66666666-6666-6666-6666-666666666666");

    public static final String FIRST_NAME = "Иван";

    public static final String LAST_NAME = "Иванов";

    public static final String MIDDLE_NAME = "Иванович";

    public static final String EMAIL = "ivanov@example.com";

    public static final String PHONE = "79137235412";

    public static final String ROOM_NUMBER = "101";

    public static final String TG_LINK = "https://t.me/ivanov";

    public static final String VK_LINK = "https://vk.com/ivanov";

    public static final String ROLE_STUDENT = "STUDENT";

    public static final String ROLE_ADMINISTRATION = "ADMINISTRATION";

    public static final String TRACE_ID = "0af7651916cd43dd8448eb211c80319c";

    public static final String LOCAL_DATE = "2026-06-09";

    public static final String BOOKING_TYPE_GYM = "GYM";

    public static final String BOOKING_TYPE_ALL = "ALL";

    public static final String MALFORMED_TOKEN = "this.is.not.a.jwt";

    public static Key signingKey(String secret) {
        return new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS512.getJcaName());
    }

    public static String validToken(UUID userId, List<String> roles) {
        return Jwts.builder()
                .setClaims(Map.of("userId", userId.toString(), "roles", roles))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 3_600_000))
                .signWith(Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    public static String validToken() {
        return validToken(USER_ID, List.of(ROLE_STUDENT));
    }

    public static String expiredToken(UUID userId, List<String> roles) {
        return Jwts.builder()
                .setClaims(Map.of("userId", userId.toString(), "roles", roles))
                .setIssuedAt(new Date(System.currentTimeMillis() - 7_200_000))
                .setExpiration(new Date(System.currentTimeMillis() - 3_600_000))
                .signWith(Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    public static String tokenWithWrongSecret(UUID userId, List<String> roles) {
        return Jwts.builder()
                .setClaims(Map.of("userId", userId.toString(), "roles", roles))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 3_600_000))
                .signWith(Keys.hmacShaKeyFor(WRONG_JWT_SECRET.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    public static String tokenWithoutUserId() {
        return Jwts.builder()
                .setClaims(Map.of("roles", List.of(ROLE_STUDENT)))
                .setExpiration(new Date(System.currentTimeMillis() + 3_600_000))
                .signWith(Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    public static String tokenWithoutRoles() {
        return Jwts.builder()
                .setClaims(Map.of("userId", USER_ID.toString()))
                .setExpiration(new Date(System.currentTimeMillis() + 3_600_000))
                .signWith(Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    public static String bearer(String token) {
        return "Bearer " + token;
    }

    public static Authentication authentication(UUID userId, List<String> roles) {
        List<GrantedAuthority> authorities = roles.stream()
                .map(role -> (GrantedAuthority) new SimpleGrantedAuthority("ROLE_" + role))
                .toList();
        return new UsernamePasswordAuthenticationToken(userId.toString(), null, authorities);
    }

    public static Authentication authentication() {
        return authentication(USER_ID, List.of(ROLE_STUDENT));
    }

    public static UserResponseWithRoleDto userWithRoleDto() {
        return new UserResponseWithRoleDto(
                USER_ID,
                FIRST_NAME,
                LAST_NAME,
                MIDDLE_NAME,
                EMAIL,
                PHONE,
                ROOM_NUMBER,
                List.of(ROLE_STUDENT),
                TG_LINK,
                VK_LINK
        );
    }

    public static UserResponseWithRoleDto userWithRoleDtoWithoutRoom() {
        return new UserResponseWithRoleDto(
                USER_ID,
                FIRST_NAME,
                LAST_NAME,
                MIDDLE_NAME,
                EMAIL,
                PHONE,
                "",
                List.of(ROLE_STUDENT),
                TG_LINK,
                VK_LINK
        );
    }

    public static UserResponseDto userResponseDto() {
        return new UserResponseDto(
                USER_ID,
                FIRST_NAME,
                LAST_NAME,
                MIDDLE_NAME,
                EMAIL,
                PHONE,
                ROOM_NUMBER,
                TG_LINK,
                VK_LINK
        );
    }

    public static UserShortResponseDto2 userShortResponseDto() {
        return new UserShortResponseDto2(
                USER_ID,
                FIRST_NAME,
                LAST_NAME,
                MIDDLE_NAME,
                TG_LINK,
                VK_LINK
        );
    }

    public static UserShortResponseDto2 responsibleShortResponseDto() {
        return new UserShortResponseDto2(
                RESPONSIBLE_ID,
                FIRST_NAME,
                LAST_NAME,
                MIDDLE_NAME,
                TG_LINK,
                VK_LINK
        );
    }

    public static BalanceResponseDto balanceResponseDto() {
        return new BalanceResponseDto(USER_ID, BigDecimal.valueOf(500.0));
    }

    public static CertificateDto certificateDto() {
        return new CertificateDto(
                DOCUMENT_ID,
                USER_ID,
                DocumentType.CERTIFICATE,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31)
        );
    }

    public static CertificateDto fluorographyDto() {
        return new CertificateDto(
                DOCUMENT_ID,
                USER_ID,
                DocumentType.FLUOROGRAPHY,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31)
        );
    }

    public static ActiveEventDto activeEventDto() {
        return new ActiveEventDto(
                BOOKING_ID,
                LocalDateTime.of(2026, 6, 9, 10, 0),
                LocalDateTime.of(2026, 6, 9, 11, 0),
                BookingStatus.BOOKED,
                "Зал"
        );
    }

    public static ActiveEventDto activeEventDto(BookingStatus status, String type) {
        return new ActiveEventDto(
                BOOKING_ID,
                LocalDateTime.of(2026, 6, 9, 10, 0),
                LocalDateTime.of(2026, 6, 9, 11, 0),
                status,
                type
        );
    }

    public static ActiveEventDtoResponse activeEventDtoResponse() {
        return new ActiveEventDtoResponse(
                BOOKING_ID,
                LocalDateTime.of(2026, 6, 9, 10, 0),
                LocalDateTime.of(2026, 6, 9, 11, 0),
                BookingStatus.BOOKED,
                "Зал",
                true
        );
    }

    public static ActiveEventWithUserDto activeEventWithUserDto() {
        return new ActiveEventWithUserDto(
                BOOKING_ID,
                USER_ID,
                LocalDateTime.of(2026, 6, 9, 10, 0),
                LocalDateTime.of(2026, 6, 9, 11, 0),
                BookingStatus.BOOKED,
                "Зал"
        );
    }

    public static TimeSlotResponseDto timeSlotResponseDto() {
        return new TimeSlotResponseDto(TIME_SLOT_ID, "10:00-11:00");
    }

    public static AvailableTimeSlotsWithResponsible availableTimeSlotsWithResponsible() {
        return new AvailableTimeSlotsWithResponsible(
                RESPONSIBLE_ID,
                List.of(timeSlotResponseDto())
        );
    }
}
