package ru.tpu.hostel.api_gateway.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.tpu.hostel.api_gateway.client.AdminClient;
import ru.tpu.hostel.api_gateway.client.BookingsClient;
import ru.tpu.hostel.api_gateway.client.UserClient;
import ru.tpu.hostel.api_gateway.dto.ActiveEventDto;
import ru.tpu.hostel.api_gateway.dto.ActiveEventDtoResponse;
import ru.tpu.hostel.api_gateway.dto.AdminResponseDto;
import ru.tpu.hostel.api_gateway.dto.BalanceResponseDto;
import ru.tpu.hostel.api_gateway.dto.CertificateDto;
import ru.tpu.hostel.api_gateway.dto.UserResponseDto;
import ru.tpu.hostel.api_gateway.dto.WholeUserResponseDto;
import ru.tpu.hostel.api_gateway.dto.UserResponseWithRoleDto;
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

@Service
@RequiredArgsConstructor
public class AgregationServiceImpl implements AgregationService {

    private final UserClient userClient;
    private final BookingsClient bookingsClient;
    private final AdminClient administrationClient;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Override
    public Mono<WholeUserResponseDto> getWholeUser(Authentication authentication) {

        UUID userId = jwtAuthenticationFilter.getUserIdFromToken(authentication);

        // Получаем данные пользователя
        Mono<UserResponseWithRoleDto> userInfoMono = userClient.getUserWithRoles(userId);

        // Получаем баланс
        Mono<BigDecimal> balanceMono = administrationClient.getBalanceShort(userId)
                .flatMap(response -> {
                    String jsonBody = response.toString().substring(
                            response.toString().indexOf("{"), response.toString().lastIndexOf("}") + 1);
                    try {
                        JSONObject jsonObject = new JSONObject(jsonBody);
                        return Mono.just(BigDecimal.valueOf(jsonObject.getDouble("balance")));
                    } catch (JSONException e) {
                        return Mono.empty();
                    }
                });

        // Получаем документы
        Mono<CertificateDto> pediculosisMono = administrationClient
                .getDocumentByType(userId, DocumentType.CERTIFICATE);
        Mono<CertificateDto> fluorographyMono = administrationClient
                .getDocumentByType(userId, DocumentType.FLUOROGRAPHY);

        // Получаем активные бронирования
        Flux<ActiveEventDto> activeBookingsFlux = Flux.concat(
                bookingsClient.getAllByStatus(BookingStatus.BOOKED, userId),
                bookingsClient.getAllByStatus(BookingStatus.IN_PROGRESS, userId)
        );

        Flux<ActiveEventDtoResponse> activeEventDtoResponseFlux = activeBookingsFlux
                .map(activeEventDto -> new ActiveEventDtoResponse(
                        activeEventDto.id(),
                        activeEventDto.startTime(),
                        activeEventDto.endTime(),
                        activeEventDto.status(),
                        activeEventDto.type(),
                        (activeEventDto.type().equals("GYM")
                                || activeEventDto.type().equals("HALL")
                                || activeEventDto.type().equals("INTERNET"))
                                && activeEventDto.status() == BookingStatus.BOOKED
                ));

        // Комбинируем все данные
        return Mono.zip(
                userInfoMono,
                balanceMono,
                pediculosisMono,
                fluorographyMono,
                activeEventDtoResponseFlux.collectList()
        ).map(tuple -> UserMapper.mapToUserResponseDto(
                tuple.getT1(), // UserResponseWithRoleDto
                tuple.getT2(), // Balance
                tuple.getT3(), // Pediculosis
                tuple.getT4(), // Fluorography
                tuple.getT5()  // List<ActiveEventDto>
        ));
    }


    @Override
    public Flux<AdminResponseDto> getAllUsers(Authentication authentication) {

        // Получаем данные всех пользователей
        Flux<UserResponseDto> usersFlux = userClient.getAllUsers();
        Flux<BalanceResponseDto> balancesFlux = administrationClient.getAllBalances();
        Flux<CertificateDto> certificatesFlux = administrationClient.getAllDocuments();

        return Flux.zip(usersFlux.collectMap(UserResponseDto::id, user -> user),
                        balancesFlux.collectMap(BalanceResponseDto::user, balance -> balance),
                        certificatesFlux.collectList())
                .flatMap(tuple -> {
                    Map<UUID, UserResponseDto> usersMap = tuple.getT1();
                    Map<UUID, BalanceResponseDto> balancesMap = tuple.getT2();
                    List<CertificateDto> certificates = tuple.getT3();

                    // Создаём результирующую карту
                    Map<UUID, AdminResponseDto> adminResponseDtoMap = new HashMap<>();
                    usersMap.forEach((id, user) -> adminResponseDtoMap.put(id, new AdminResponseDto(
                            user.id(),
                            user.firstName(),
                            user.lastName(),
                            user.middleName(),
                            user.roomNumber(),
                            null, null, null
                    )));

                    // Добавляем балансы
                    balancesMap.forEach((id, balance) -> {
                        AdminResponseDto userData = adminResponseDtoMap.get(id);
                        if (userData != null) {
                            adminResponseDtoMap.put(id, new AdminResponseDto(
                                    userData.id(),
                                    userData.firstName(),
                                    userData.lastName(),
                                    userData.middleName(),
                                    userData.room(),
                                    userData.pediculosis(),
                                    userData.fluorography(),
                                    balance.balance()
                            ));
                        }
                    });

                    // Добавляем документы
                    for (CertificateDto certificate : certificates) {
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

                    // Преобразуем значения карты в Flux
                    return Mono.just(adminResponseDtoMap.values()).flatMapMany(Flux::fromIterable);
                });
    }



}
