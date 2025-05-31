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
import ru.tpu.hostel.api_gateway.enums.EventType;
import ru.tpu.hostel.api_gateway.exception.ServiceException;
import ru.tpu.hostel.api_gateway.mapper.UserMapper;
import ru.tpu.hostel.api_gateway.service.AgregationService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgregationServiceImpl implements AgregationService {

    private static final UserResponseWithRoleDto DEFAULT_USER_RESPONSE_WITH_ROLE = new UserResponseWithRoleDto(
            null,
            "",
            "",
            "",
            "",
            "",
            "",
            List.of()
    );

    private static final CertificateDto DEFAULT_CERTIFICATE_DTO = new CertificateDto(
            null,
            null,
            DocumentType.CERTIFICATE,
            LocalDate.of(0, 1, 1),
            LocalDate.of(0, 1, 1)
    );

    private static final CertificateDto DEFAULT_FLUOROGRAPHY_DTO = new CertificateDto(
            null,
            null,
            DocumentType.FLUOROGRAPHY,
            LocalDate.of(0, 1, 1),
            LocalDate.of(0, 1, 1)
    );

    private static final ActiveEventDto DEFAULT_ACTIVE_EVENT_DTO = new ActiveEventDto(
            null,
            LocalDateTime.of(0, 1, 1, 0, 0),
            LocalDateTime.of(0, 1, 1, 0, 0),
            null,
            ""
    );

    private static final List<String> CLOSABLE_EVENT_TYPES = List.of(
            EventType.GYM.getEventTypeName(),
            EventType.HALL.getEventTypeName(),
            EventType.INTERNET.getEventTypeName()
    );

    private static final int MAX_ERRORS_COUNT_ON_GET_WHOLE_USER = 8;

    private final UserClient userClient;

    private final BookingsClient bookingsClient;

    private final AdminClient administrationClient;

    private final SchedulesClient schedulesClient;

    private final JwtService jwtService;

    @Override
    public Mono<WholeUserResponseDto> getWholeUser(Authentication authentication, String roomFromRequest) {
        AtomicInteger currentErrorsCount = new AtomicInteger(0);
        AtomicBoolean requestFailed = new AtomicBoolean(false);
        AtomicBoolean userRequestFailed = new AtomicBoolean(false);
        AtomicBoolean balanceRequestFailed = new AtomicBoolean(false);
        AtomicBoolean certificateRequestFailed = new AtomicBoolean(false);
        AtomicBoolean fluorographyRequestFailed = new AtomicBoolean(false);

        UUID userId = jwtService.getUserIdFromToken(authentication);

        // Инфа о юзере (кэшируем, так как к userInfoMono будем обращаться дважды)
        Mono<UserResponseWithRoleDto> userInfoMono = userClient.getUserWithRoles(userId)
                .onErrorResume(t -> {
                    userRequestFailed.set(true);
                    return handleMonoError(t, currentErrorsCount, DEFAULT_USER_RESPONSE_WITH_ROLE);
                })
                .cache();

        // Баланс
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
                })
                .onErrorResume(t -> {
                    balanceRequestFailed.set(true);
                    return handleMonoError(t, currentErrorsCount, BigDecimal.ZERO);
                });

        // Справки
        Mono<Optional<CertificateDto>> pediculosisMono = administrationClient.getDocumentByType(
                        userId,
                        DocumentType.CERTIFICATE
                )
                .map(Optional::of)
                .onErrorResume(t -> {
                    certificateRequestFailed.set(true);
                    return handleMonoError(t, currentErrorsCount);
                });
        Mono<Optional<CertificateDto>> fluorographyMono = administrationClient.getDocumentByType(
                        userId,
                        DocumentType.FLUOROGRAPHY
                )
                .map(Optional::of)
                .onErrorResume(t -> {
                    fluorographyRequestFailed.set(true);
                    return handleMonoError(t, currentErrorsCount);
                });

        // Активные ивенты (брони и дежурства)
        Flux<ActiveEventDto> bookedBookings = bookingsClient.getAllByStatus(BookingStatus.BOOKED, userId)
                .onErrorResume(t -> handleFluxError(t, currentErrorsCount));
        Flux<ActiveEventDto> processedBookings = bookingsClient.getAllByStatus(BookingStatus.IN_PROGRESS, userId)
                .onErrorResume(t -> handleFluxError(t, currentErrorsCount));
        Flux<ActiveEventDto> kitchenSchedules = userInfoMono.flatMapMany(user -> {
                    String identifier;
                    if (!roomFromRequest.isEmpty()) {
                        identifier = roomFromRequest;
                    } else {
                        identifier = user.roomNumber().isEmpty()
                                ? userId.toString() // неэффективный вариант
                                : user.roomNumber(); // эффективный вариант
                    }
                    return schedulesClient.getActiveKitchenSchedules(identifier);
                })
                .onErrorResume(t -> handleFluxError(t, currentErrorsCount));
        Flux<ActiveEventDto> responsibles = schedulesClient.getActiveResponsibles(userId)
                .onErrorResume(t -> handleFluxError(t, currentErrorsCount));

        // Объединение активных ивентов в один поток
        Flux<ActiveEventDto> activeEventsFlux = Flux.concat(
                bookedBookings,
                processedBookings,
                kitchenSchedules,
                responsibles
        );

        // Преобразование ивентов в ДТО для ответа
        Flux<ActiveEventDtoResponse> activeEventDtoResponseFlux = activeEventsFlux
                .map(activeEventDto -> new ActiveEventDtoResponse(
                                activeEventDto.id(),
                                activeEventDto.startTime(),
                                activeEventDto.endTime(),
                                activeEventDto.status(),
                                activeEventDto.type(),
                                CLOSABLE_EVENT_TYPES.contains(activeEventDto.type())
                                        && activeEventDto.status() == BookingStatus.BOOKED
                        )
                );

        // Преобразование всех данных в единую ДТО для ответа
        return Mono.zip(
                userInfoMono,
                balanceMono,
                pediculosisMono,
                fluorographyMono,
                activeEventDtoResponseFlux.collectList()
        ).handle((tuple, sink) -> {
            requestFailed.set(
                    userRequestFailed.get()
                            && balanceRequestFailed.get()
                            && certificateRequestFailed.get()
                            && fluorographyRequestFailed.get()
            );
            /*
            Если провалились все запросы или все, кроме активных ивентов,
            но при этом ивенты все равно пустые, то кидаем исключение.
            Исключение во втором случае кидаем, так как ответ все равно будет пустым, как при падении всех запросов
            */
            if (currentErrorsCount.get() == MAX_ERRORS_COUNT_ON_GET_WHOLE_USER
                    || (tuple.getT5().isEmpty() && requestFailed.get())) {
                sink.error(new ServiceException.BadGateway("Сервис временно не доступен. Повторите попытку позже"));
                return;
            }
            sink.next(UserMapper.mapToUserResponseDto(
                    tuple.getT1(),
                    tuple.getT2(),
                    tuple.getT3().orElse(null),
                    tuple.getT4().orElse(null),
                    tuple.getT5()
            ));
        });
    }

    private <T> Mono<T> handleMonoError(Throwable e, AtomicInteger counter, T defaultValue) {
        counter.incrementAndGet();
        logWarning(e);
        return Mono.just(defaultValue);
    }

    private <T> Mono<Optional<T>> handleMonoError(Throwable e, AtomicInteger counter) {
        counter.incrementAndGet();
        logWarning(e);
        return Mono.just(Optional.empty());
    }

    private <T> Flux<T> handleFluxError(Throwable e, AtomicInteger counter) {
        counter.incrementAndGet();
        logWarning(e);
        return Flux.empty();
    }

    private void logWarning(Throwable e) {
        log.warn("Ошибка запроса: {}", e.getMessage());
    }

    // На эту дичь даже смотреть не хочется
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

        return bookings
                .groupBy(booking -> booking.startTime() + "-" + booking.endTime())
                .flatMap(groupedBookings -> groupedBookings.collectList()
                        .flatMapMany(bookingsList -> {
                            List<UUID> userIds = bookingsList.stream()
                                    .map(ActiveEventWithUserDto::userId)
                                    .toList();

                            return userClient.getAllUsersWithIdsShort(userIds)
                                    .onErrorResume(e -> Flux.empty())
                                    .collectList()
                                    .map(users -> {
                                        List<UserShortResponseWithBookingIdDto> usersWithBookingIds = users.stream()
                                                .map(user -> {
                                                    UUID bookingId = bookingsList.stream()
                                                            .filter(booking ->
                                                                    booking.userId().equals(user.id()))
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
                                                .toList();

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
                .getAvailableTimeSlots(localDate, type, jwtService.getUserIdFromToken(authentication));
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
