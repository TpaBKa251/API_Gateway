package ru.tpu.hostel.api_gateway.mapper;

import org.springframework.stereotype.Component;
import ru.tpu.hostel.api_gateway.dto.CertificateDto;
import ru.tpu.hostel.api_gateway.dto.UserResponseWithRoleDto;
import ru.tpu.hostel.api_gateway.dto.WholeUserResponseDto;
import ru.tpu.hostel.api_gateway.dto_library.response.BookingResponseDto;
import ru.tpu.hostel.api_gateway.dto_library.response.DocumentResponseDto;

import java.math.BigDecimal;
import java.util.List;

@Component
public class UserMapper {

    public static WholeUserResponseDto mapToUserResponseDto(
            UserResponseWithRoleDto userResponseWithRoleDto,
            BigDecimal balance,
            DocumentResponseDto pediculosis,
            DocumentResponseDto fluorography,
            List<BookingResponseDto> activeEvents
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
