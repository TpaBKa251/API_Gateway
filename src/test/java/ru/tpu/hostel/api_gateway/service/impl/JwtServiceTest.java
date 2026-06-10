package ru.tpu.hostel.api_gateway.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;
import ru.tpu.hostel.api_gateway.TestData;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", TestData.JWT_SECRET);
    }

    @Test
    void getUserIdFromTokenWithAuthentication() {
        Authentication authentication = TestData.authentication(TestData.USER_ID, List.of(TestData.ROLE_STUDENT));

        UUID result = jwtService.getUserIdFromToken(authentication);

        assertThat(result).isEqualTo(TestData.USER_ID);
    }

    @Test
    void getUserIdFromTokenWithValidToken() {
        String token = TestData.validToken(TestData.USER_ID, List.of(TestData.ROLE_STUDENT));

        UUID result = jwtService.getUserIdFromToken(token);

        assertThat(result).isEqualTo(TestData.USER_ID);
    }

    @Test
    void getUserIdFromTokenWhenTokenIsInvalid() {
        assertThatThrownBy(() -> jwtService.getUserIdFromToken(TestData.MALFORMED_TOKEN))
                .isInstanceOf(Exception.class);
    }

    @Test
    void getUserIdFromTokenWhenSignatureIsWrong() {
        String token = TestData.tokenWithWrongSecret(TestData.USER_ID, List.of(TestData.ROLE_STUDENT));

        assertThatThrownBy(() -> jwtService.getUserIdFromToken(token))
                .isInstanceOf(Exception.class);
    }

    @Test
    void getRolesFromTokenWithSingleRole() {
        Authentication authentication = TestData.authentication(TestData.USER_ID, List.of(TestData.ROLE_STUDENT));

        String result = jwtService.getRolesFromToken(authentication);

        assertThat(result).isEqualTo(TestData.ROLE_STUDENT);
    }

    @Test
    void getRolesFromTokenWithMultipleRoles() {
        Authentication authentication = TestData.authentication(
                TestData.USER_ID,
                List.of(TestData.ROLE_STUDENT, TestData.ROLE_ADMINISTRATION)
        );

        String result = jwtService.getRolesFromToken(authentication);

        assertThat(result).contains(TestData.ROLE_STUDENT, TestData.ROLE_ADMINISTRATION);
        assertThat(result).doesNotContain("ROLE_");
    }

    @Test
    void getListRolesFromTokenWithMultipleRoles() {
        Authentication authentication = TestData.authentication(
                TestData.USER_ID,
                List.of(TestData.ROLE_STUDENT, TestData.ROLE_ADMINISTRATION)
        );

        List<String> result = jwtService.getListRolesFromToken(authentication);

        assertThat(result).containsExactlyInAnyOrder(TestData.ROLE_STUDENT, TestData.ROLE_ADMINISTRATION);
    }
}
