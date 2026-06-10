package ru.tpu.hostel.api_gateway.mapper;

import org.junit.jupiter.api.Test;
import ru.tpu.hostel.api_gateway.TestData;
import ru.tpu.hostel.api_gateway.dto.ActiveEventDtoResponse;
import ru.tpu.hostel.api_gateway.dto.WholeUserResponseDto;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    @Test
    void mapToUserResponseDtoMapsAllFields() {
        ActiveEventDtoResponse event = TestData.activeEventDtoResponse();

        WholeUserResponseDto result = UserMapper.mapToUserResponseDto(
                TestData.userWithRoleDto(),
                BigDecimal.valueOf(500),
                TestData.certificateDto(),
                TestData.fluorographyDto(),
                List.of(event)
        );

        assertThat(result.id()).isEqualTo(TestData.USER_ID);
        assertThat(result.firstName()).isEqualTo(TestData.FIRST_NAME);
        assertThat(result.lastName()).isEqualTo(TestData.LAST_NAME);
        assertThat(result.middleName()).isEqualTo(TestData.MIDDLE_NAME);
        assertThat(result.roomNumber()).isEqualTo(TestData.ROOM_NUMBER);
        assertThat(result.role()).containsExactly(TestData.ROLE_STUDENT);
        assertThat(result.balance()).isEqualByComparingTo(BigDecimal.valueOf(500));
        assertThat(result.pediculosis()).isEqualTo(TestData.certificateDto());
        assertThat(result.fluorography()).isEqualTo(TestData.fluorographyDto());
        assertThat(result.activeEvents()).containsExactly(event);
        assertThat(result.tgLink()).isEqualTo(TestData.TG_LINK);
        assertThat(result.vkLink()).isEqualTo(TestData.VK_LINK);
    }

    @Test
    void mapToUserResponseDtoWhenCertificatesAreNull() {
        WholeUserResponseDto result = UserMapper.mapToUserResponseDto(
                TestData.userWithRoleDto(),
                BigDecimal.ZERO,
                null,
                null,
                List.of()
        );

        assertThat(result.pediculosis()).isNull();
        assertThat(result.fluorography()).isNull();
        assertThat(result.activeEvents()).isEmpty();
    }
}
