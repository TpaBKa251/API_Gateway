package ru.tpu.hostel.api_gateway.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.tpu.hostel.api_gateway.client.AdminClient;
import ru.tpu.hostel.api_gateway.client.BookingsClient;
import ru.tpu.hostel.api_gateway.client.UserClient;
import ru.tpu.hostel.api_gateway.dto.AdminResponseDto;
import ru.tpu.hostel.api_gateway.dto.BalanceResponseDto;
import ru.tpu.hostel.api_gateway.dto.UserResponseDto;
import ru.tpu.hostel.api_gateway.dto.UserResponseWithRoleDto;
import ru.tpu.hostel.api_gateway.dto.WholeUserResponseDto;
import ru.tpu.hostel.api_gateway.dto_library.response.BookingResponseDto;
import ru.tpu.hostel.api_gateway.dto_library.response.DocumentResponseDto;
import ru.tpu.hostel.api_gateway.enums.BookingStatus;
import ru.tpu.hostel.api_gateway.enums.DocumentType;
import ru.tpu.hostel.api_gateway.filter.JwtAuthenticationFilter;
import ru.tpu.hostel.api_gateway.mapper.UserMapper;
import ru.tpu.hostel.api_gateway.service.AgregationService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgregationServiceImpl implements AgregationService {

    private final UserClient userClient;
    private final BookingsClient bookingsClient;
    private final AdminClient administrationClient;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Override
    public WholeUserResponseDto getWholeUser(Authentication authentication) {

        UUID userId = jwtAuthenticationFilter.getUserIdFromToken(authentication);
        log.info(userId.toString());

        UserResponseWithRoleDto userInfo = userClient.getUserWithRoles(userId);
        String responseBalance = administrationClient.getBalanceShort(userId).toString();

        String jsonBody = responseBalance.substring(
                responseBalance.indexOf("{"), responseBalance.lastIndexOf("}") + 1
        );
        JSONObject jsonObject;
        BigDecimal balance = null;
        try {
            jsonObject = new JSONObject(jsonBody);
            balance = BigDecimal.valueOf(jsonObject.getDouble("balance"));
        } catch (JSONException ignored) {
        }

        DocumentResponseDto pediculosis = administrationClient.getDocumentByType(userId, DocumentType.CERTIFICATE);
        DocumentResponseDto fluorography = administrationClient.getDocumentByType(userId, DocumentType.FLUOROGRAPHY);

        List<BookingResponseDto> activeBookings = new ArrayList<>();
        activeBookings.addAll(bookingsClient.getAllByStatus(BookingStatus.BOOKED, userId));
        activeBookings.addAll(bookingsClient.getAllByStatus(BookingStatus.IN_PROGRESS, userId));


        return UserMapper.mapToUserResponseDto(
                userInfo,
                balance,
                pediculosis,
                fluorography,
                activeBookings
        );
    }

    @Override
    public List<AdminResponseDto> getAllUsers(Authentication authentication) {
        Map<UUID, AdminResponseDto> adminResponseDtoMap = new HashMap<>();

        List<UserResponseDto> users = userClient.getAllUsers();
        List<BalanceResponseDto> balances = administrationClient.getAllBalances();
        List<DocumentResponseDto> certificates = administrationClient.getAllDocuments();

        for (UserResponseDto user : users) {
            adminResponseDtoMap.put(user.id(), new AdminResponseDto(
                    user.id(),
                    user.firstName(),
                    user.lastName(),
                    user.middleName(),
                    user.roomNumber(),
                    null,
                    null,
                    null
            ));
        }

        for (BalanceResponseDto balance : balances) {
            AdminResponseDto userData = adminResponseDtoMap.get(balance.user());

            if (userData != null) {
                adminResponseDtoMap.put(balance.user(), new AdminResponseDto(
                        userData.id(),
                        userData.firstName(),
                        userData.lastName(),
                        userData.middleName(),
                        userData.room(),
                        null,
                        null,
                        balance.balance()
                ));
            }
        }

        for (DocumentResponseDto certificate : certificates) {
            AdminResponseDto userWithBalanceData = adminResponseDtoMap.get(certificate.user());

            if (userWithBalanceData != null) {
                if (certificate.type() == DocumentType.CERTIFICATE) {
                    adminResponseDtoMap.put(certificate.user(), new AdminResponseDto(
                            userWithBalanceData.id(),
                            userWithBalanceData.firstName(),
                            userWithBalanceData.lastName(),
                            userWithBalanceData.middleName(),
                            userWithBalanceData.room(),
                            certificate,
                            userWithBalanceData.fluorography(),
                            userWithBalanceData.balance()
                    ));
                } else {
                    adminResponseDtoMap.put(certificate.user(), new AdminResponseDto(
                            userWithBalanceData.id(),
                            userWithBalanceData.firstName(),
                            userWithBalanceData.lastName(),
                            userWithBalanceData.middleName(),
                            userWithBalanceData.room(),
                            userWithBalanceData.pediculosis(),
                            certificate,
                            userWithBalanceData.balance()
                    ));
                }
            }
        }

        return new ArrayList<>(adminResponseDtoMap.values());
    }
}
