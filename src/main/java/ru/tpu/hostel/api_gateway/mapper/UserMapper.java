package ru.tpu.hostel.api_gateway.mapper;

import org.springframework.stereotype.Component;
import ru.tpu.hostel.api_gateway.dto.ActiveEventDtoResponse;
import ru.tpu.hostel.api_gateway.dto.CertificateDto;
import ru.tpu.hostel.api_gateway.dto.UserResponseWithRoleDto;
import ru.tpu.hostel.api_gateway.dto.WholeUserResponseDto;

import java.math.BigDecimal;
import java.util.List;

@Component
public class UserMapper {

    public static WholeUserResponseDto mapToUserResponseDto(
            UserResponseWithRoleDto userResponseWithRoleDto,
            BigDecimal balance,
            CertificateDto pediculosis,
            CertificateDto fluorography,
            List<ActiveEventDtoResponse> activeEvents
    ) {
        return new WholeUserResponseDto(
                userResponseWithRoleDto.id(),
                userResponseWithRoleDto.firstName(),
                userResponseWithRoleDto.lastName(),
                userResponseWithRoleDto.middleName(),
                userResponseWithRoleDto.roomNumber(),
                userResponseWithRoleDto.roles(),
                balance,
                pediculosis,
                fluorography,
                activeEvents
        );
    }
}
