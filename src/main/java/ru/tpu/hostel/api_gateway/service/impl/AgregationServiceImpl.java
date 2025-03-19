package ru.tpu.hostel.api_gateway.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.tpu.hostel.api_gateway.client.AdminClient;
import ru.tpu.hostel.api_gateway.client.BookingsClient;
import ru.tpu.hostel.api_gateway.client.SchedulesClient;
import ru.tpu.hostel.api_gateway.client.UserClient;
import ru.tpu.hostel.api_gateway.dto.ActiveEventDto;
import ru.tpu.hostel.api_gateway.dto.ActiveEventDtoResponse;
import ru.tpu.hostel.api_gateway.dto.ActiveEventWithUserDto;
import ru.tpu.hostel.api_gateway.dto.AdminResponseDto;
import ru.tpu.hostel.api_gateway.dto.AvailableTimeSlotsDto;
import ru.tpu.hostel.api_gateway.dto.AvailableTimeSlotsWithResponsible;
import ru.tpu.hostel.api_gateway.dto.BalanceResponseDto;
import ru.tpu.hostel.api_gateway.dto.BookingResponseWithUsersDto;
import ru.tpu.hostel.api_gateway.dto.CertificateDto;
import ru.tpu.hostel.api_gateway.dto.UserResponseDto;
import ru.tpu.hostel.api_gateway.dto.UserResponseWithRoleDto;
import ru.tpu.hostel.api_gateway.dto.UserShortResponseDto2;
import ru.tpu.hostel.api_gateway.dto.UserShortResponseWithBookingIdDto;
import ru.tpu.hostel.api_gateway.dto.WholeUserResponseDto;
import ru.tpu.hostel.api_gateway.enums.BookingStatus;
import ru.tpu.hostel.api_gateway.enums.DocumentType;
import ru.tpu.hostel.api_gateway.filter.JwtAuthenticationFilter;
import ru.tpu.hostel.api_gateway.mapper.UserMapper;
import ru.tpu.hostel.api_gateway.service.AgregationService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgregationServiceImpl implements AgregationService {

    private final UserClient userClient;
    private final BookingsClient bookingsClient;
    private final AdminClient administrationClient;
    private final SchedulesClient schedulesClient;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Override
    public Mono<WholeUserResponseDto> getWholeUser(Authentication authentication) {

        UUID userId = jwtAuthenticationFilter.getUserIdFromToken(authentication);

        Mono<UserResponseWithRoleDto> userInfoMono = userClient.getUserWithRoles(userId);

        Mono<BigDecimal> balanceMono = administrationClient.getBalanceShort(userId)
                .flatMap(response -> {
                    String jsonBody = response.toString().substring(
                            response.toString().indexOf("{"), response.toString().lastIndexOf("}") + 1);
                    try {
                        JSONObject jsonObject = new JSONObject(jsonBody);
                        return Mono.just(BigDecimal.valueOf(jsonObject.getDouble("balance")));
                    } catch (JSONException e) {
                        return Mono.just(BigDecimal.ZERO);
                    }
                });

        Mono<CertificateDto> pediculosisMono = administrationClient
                .getDocumentByType(userId, DocumentType.CERTIFICATE);
        Mono<CertificateDto> fluorographyMono = administrationClient
                .getDocumentByType(userId, DocumentType.FLUOROGRAPHY);

        Flux<ActiveEventDto> activeBookingsFlux = Flux.concat(
                bookingsClient.getAllByStatus(BookingStatus.BOOKED, userId),
                bookingsClient.getAllByStatus(BookingStatus.IN_PROGRESS, userId),
                schedulesClient.getActiveKitchenSchedules(userId)
        );

        Flux<ActiveEventDtoResponse> activeEventDtoResponseFlux = activeBookingsFlux
                .map(activeEventDto -> new ActiveEventDtoResponse(
                        activeEventDto.id(),
                        activeEventDto.startTime(),
                        activeEventDto.endTime(),
                        activeEventDto.status(),
                        activeEventDto.type(),
                        (activeEventDto.type().equals("Тренажерный зал")
                                || activeEventDto.type().equals("Зал")
                                || activeEventDto.type().equals("Интернет"))
                                && activeEventDto.status() == BookingStatus.BOOKED
                ));

        return Mono.zip(
                userInfoMono,
                balanceMono,
                pediculosisMono,
                fluorographyMono,
                activeEventDtoResponseFlux.collectList()
        ).map(tuple -> UserMapper.mapToUserResponseDto(
                tuple.getT1(),
                tuple.getT2(),
                tuple.getT3(),
                tuple.getT4(),
                tuple.getT5()
        ));
    }


    @Override
    public Flux<AdminResponseDto> getAllUsers(
            Authentication authentication,
            String page,
            String size,
            String firstName,
            String lastName,
            String middleName,
            String room,
            String negative,
            String value,
            String fluraPast,
            String fluraDate,
            String certPast,
            String certDate
    ) {

        Flux<UserResponseDto> usersFlux;
        Flux<BalanceResponseDto> balancesFlux;
        Flux<CertificateDto> certificatesFlux;

        // Фильтр по юзерам
        if (!firstName.isEmpty() || !lastName.isEmpty() || !middleName.isEmpty() || !room.isEmpty()) {
            usersFlux = userClient.getAllUsers(
                    Integer.parseInt(page),
                    Integer.parseInt(size),
                    firstName,
                    lastName,
                    middleName,
                    room
            );

            Mono<List<UUID>> userIdsMono = usersFlux
                    .map(UserResponseDto::id)
                    .collectList();

            balancesFlux = userIdsMono.flatMapMany(administrationClient::getAllBalancesByUsers);
            certificatesFlux = userIdsMono.flatMapMany(administrationClient::getAllDocumentsByUsers);
        } else if (negative != null && !negative.isEmpty()) { // Фильтр по балансу
            balancesFlux = administrationClient.getAllBalances(
                    Integer.parseInt(page),
                    Integer.parseInt(size),
                    Boolean.parseBoolean(negative),
                    new BigDecimal(value)
            );

            Mono<List<UUID>> userIdsMono = balancesFlux
                    .map(BalanceResponseDto::user)
                    .collectList();

            usersFlux = userIdsMono.flatMapMany(userClient::getAllUsersWithIds);
            certificatesFlux = userIdsMono.flatMapMany(administrationClient::getAllDocumentsByUsers);
        } else if (fluraPast != null && fluraDate != null || certPast != null && certDate != null) { // Имба фильр по спракам
            certificatesFlux = administrationClient.getAllDocuments(
                    Integer.parseInt(page),
                    Integer.parseInt(size),
                    fluraPast == null ? null : Boolean.parseBoolean(fluraPast),
                    fluraDate == null ? null : LocalDate.parse(fluraDate),
                    certPast == null ? null : Boolean.parseBoolean(certPast),
                    certDate == null ? null : LocalDate.parse(certDate)
            );

            Mono<List<UUID>> userIdsMono = certificatesFlux
                    .map(CertificateDto::user)
                    .collectList();

            usersFlux = userIdsMono.flatMapMany(userClient::getAllUsersWithIds);
            balancesFlux = userIdsMono.flatMapMany(administrationClient::getAllBalancesByUsers);
        } else { // Классика без фильтров
            usersFlux = userClient.getAllUsers(
                    Integer.parseInt(page),
                    Integer.parseInt(size),
                    "",
                    "",
                    "",
                    ""
            );

            balancesFlux = administrationClient.getAllBalances(
                    Integer.parseInt(page),
                    Integer.parseInt(size),
                    null,
                    null
            );

            certificatesFlux = administrationClient.getAllDocuments(
                    Integer.parseInt(page),
                    Integer.parseInt(size),
                    null,
                    null,
                    null,
                    null
            );
        }

        return Flux.zip(
                usersFlux,
                balancesFlux,
                certificatesFlux.buffer(2)
        ).map(tuple -> {
            UserResponseDto user = tuple.getT1();
            BalanceResponseDto balance = tuple.getT2();
            List<CertificateDto> documents = tuple.getT3();

            CertificateDto pediculosis = documents.stream()
                    .filter(doc -> doc.type() == DocumentType.CERTIFICATE)
                    .findFirst()
                    .orElse(new CertificateDto(null, null, null, null, null));

            CertificateDto fluorography = documents.stream()
                    .filter(doc -> doc.type() == DocumentType.FLUOROGRAPHY)
                    .findFirst()
                    .orElse(new CertificateDto(null, null, null, null, null));

            return new AdminResponseDto(
                    user.id(),
                    user.firstName(),
                    user.lastName(),
                    user.middleName(),
                    user.roomNumber(),
                    pediculosis,
                    fluorography,
                    balance.balance()
            );
        });

    }

    @Override
    public Flux<BookingResponseWithUsersDto> getAllBookings(String type, String localDate) {
        Flux<ActiveEventWithUserDto> bookings = type.equals("ALL")
                ? bookingsClient.getAllByDate(localDate)
                : bookingsClient.getAllByTypeAndDate(type, localDate);

        if (bookings.hasElements() != Mono.just(Boolean.TRUE)) {
            return Flux.empty();
        }

        return bookings
                .groupBy(booking -> booking.startTime() + "-" + booking.endTime())
                .flatMap(groupedBookings -> groupedBookings.collectList()
                        .flatMapMany(bookingsList -> {
                            List<UUID> userIds = bookingsList.stream()
                                    .map(ActiveEventWithUserDto::userId)
                                    .toList();

                            return userClient.getAllUsersWithIdsShort(userIds)
                                    .collectList()
                                    .map(users -> {
                                        List<UserShortResponseWithBookingIdDto> usersWithBookingIds = users.stream()
                                                .map(user -> {
                                                    UUID bookingId = bookingsList.stream()
                                                            .filter(booking -> booking.userId().equals(user.id()))
                                                            .findFirst()
                                                            .map(ActiveEventWithUserDto::id)
                                                            .orElse(null);

                                                    return new UserShortResponseWithBookingIdDto(
                                                            user.firstName(),
                                                            user.lastName(),
                                                            user.middleName(),
                                                            bookingId
                                                    );
                                                })
                                                .collect(Collectors.toList());

                                        return new BookingResponseWithUsersDto(
                                                bookingsList.get(0).startTime(),
                                                bookingsList.get(0).endTime(),
                                                bookingsList.get(0).type(),
                                                usersWithBookingIds
                                        );
                                    });
                        })
                );
    }

    @Override
    public Mono<AvailableTimeSlotsDto> getAllAvailableTimeSlots(String type, String localDate, Authentication authentication) {
        Mono<AvailableTimeSlotsWithResponsible> availableTimeSlotsWithResponsible = bookingsClient
                .getAvailableTimeSlots(localDate, type, jwtAuthenticationFilter.getUserIdFromToken(authentication));
        Mono<UserShortResponseDto2> responsible = availableTimeSlotsWithResponsible.flatMap(
                response -> userClient.getUserWithIdShort(response.responsibleId())
        );

        return Mono.zip(
                responsible,
                availableTimeSlotsWithResponsible
        ).map(tuple -> new AvailableTimeSlotsDto(
                tuple.getT1().firstName(),
                tuple.getT1().lastName(),
                tuple.getT1().middleName(),
                tuple.getT2().timeSlots()
        ));
    }
}
