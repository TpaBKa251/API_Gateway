package ru.tpu.hostel.api_gateway.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.tpu.hostel.api_gateway.TestData;
import ru.tpu.hostel.api_gateway.client.AdminClient;
import ru.tpu.hostel.api_gateway.client.BookingsClient;
import ru.tpu.hostel.api_gateway.client.SchedulesClient;
import ru.tpu.hostel.api_gateway.client.UserClient;
import ru.tpu.hostel.api_gateway.dto.AdminResponseDto;
import ru.tpu.hostel.api_gateway.dto.AvailableTimeSlotsDto;
import ru.tpu.hostel.api_gateway.dto.BookingResponseWithUsersDto;
import ru.tpu.hostel.api_gateway.dto.WholeUserResponseDto;
import ru.tpu.hostel.api_gateway.enums.BookingStatus;
import ru.tpu.hostel.api_gateway.enums.DocumentType;
import ru.tpu.hostel.api_gateway.exception.ServiceException;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AgregationServiceImplTest {

    @Mock
    private UserClient userClient;

    @Mock
    private BookingsClient bookingsClient;

    @Mock
    private AdminClient administrationClient;

    @Mock
    private SchedulesClient schedulesClient;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AgregationServiceImpl service;

    private Authentication authentication;

    @BeforeEach
    void setUp() {
        authentication = TestData.authentication(TestData.USER_ID, List.of(TestData.ROLE_STUDENT));
        when(jwtService.getUserIdFromToken(authentication)).thenReturn(TestData.USER_ID);
    }

    @Test
    void getWholeUserWhenAllRequestsSucceed() {
        when(userClient.getUserWithRoles(TestData.USER_ID)).thenReturn(Mono.just(TestData.userWithRoleDto()));
        when(administrationClient.getBalanceShort(TestData.USER_ID))
                .thenReturn(Mono.just("ResponseEntity[{\"balance\":500.0}]"));
        when(administrationClient.getDocumentByType(TestData.USER_ID, DocumentType.CERTIFICATE))
                .thenReturn(Mono.just(TestData.certificateDto()));
        when(administrationClient.getDocumentByType(TestData.USER_ID, DocumentType.FLUOROGRAPHY))
                .thenReturn(Mono.just(TestData.fluorographyDto()));
        when(bookingsClient.getAllByStatus(BookingStatus.BOOKED, TestData.USER_ID))
                .thenReturn(Flux.just(TestData.activeEventDto(BookingStatus.BOOKED, "Зал")));
        when(bookingsClient.getAllByStatus(BookingStatus.IN_PROGRESS, TestData.USER_ID))
                .thenReturn(Flux.empty());
        when(schedulesClient.getActiveKitchenSchedules(anyString())).thenReturn(Flux.empty());
        when(schedulesClient.getActiveResponsibles(TestData.USER_ID)).thenReturn(Flux.empty());

        StepVerifier.create(service.getWholeUser(authentication, ""))
                .assertNext(response -> {
                    assertThat(response.id()).isEqualTo(TestData.USER_ID);
                    assertThat(response.balance()).isEqualByComparingTo(BigDecimal.valueOf(500.0));
                    assertThat(response.activeEvents()).hasSize(1);
                    assertThat(response.activeEvents().get(0).closable()).isTrue();
                })
                .verifyComplete();
    }

    @Test
    void getWholeUserWhenBalanceJsonIsInvalid() {
        when(userClient.getUserWithRoles(TestData.USER_ID)).thenReturn(Mono.just(TestData.userWithRoleDto()));
        when(administrationClient.getBalanceShort(TestData.USER_ID))
                .thenReturn(Mono.just("ResponseEntity[{not-a-json}]"));
        when(administrationClient.getDocumentByType(TestData.USER_ID, DocumentType.CERTIFICATE))
                .thenReturn(Mono.just(TestData.certificateDto()));
        when(administrationClient.getDocumentByType(TestData.USER_ID, DocumentType.FLUOROGRAPHY))
                .thenReturn(Mono.just(TestData.fluorographyDto()));
        when(bookingsClient.getAllByStatus(any(), eq(TestData.USER_ID))).thenReturn(Flux.empty());
        when(schedulesClient.getActiveKitchenSchedules(anyString())).thenReturn(Flux.empty());
        when(schedulesClient.getActiveResponsibles(TestData.USER_ID)).thenReturn(Flux.empty());

        StepVerifier.create(service.getWholeUser(authentication, ""))
                .assertNext(response -> assertThat(response.balance()).isEqualByComparingTo(BigDecimal.ZERO))
                .verifyComplete();
    }

    @Test
    void getWholeUserUsesRoomFromRequestForKitchenSchedules() {
        when(userClient.getUserWithRoles(TestData.USER_ID)).thenReturn(Mono.just(TestData.userWithRoleDto()));
        when(administrationClient.getBalanceShort(TestData.USER_ID))
                .thenReturn(Mono.just("ResponseEntity[{\"balance\":0.0}]"));
        when(administrationClient.getDocumentByType(any(), any())).thenReturn(Mono.just(TestData.certificateDto()));
        when(bookingsClient.getAllByStatus(any(), any())).thenReturn(Flux.empty());
        when(schedulesClient.getActiveKitchenSchedules(eq("202"))).thenReturn(Flux.empty());
        when(schedulesClient.getActiveResponsibles(TestData.USER_ID)).thenReturn(Flux.empty());

        StepVerifier.create(service.getWholeUser(authentication, "202"))
                .assertNext(response -> assertThat(response.id()).isEqualTo(TestData.USER_ID))
                .verifyComplete();
    }

    @Test
    void getWholeUserUsesUserIdWhenRoomIsEmptyEverywhere() {
        when(userClient.getUserWithRoles(TestData.USER_ID))
                .thenReturn(Mono.just(TestData.userWithRoleDtoWithoutRoom()));
        when(administrationClient.getBalanceShort(TestData.USER_ID))
                .thenReturn(Mono.just("ResponseEntity[{\"balance\":0.0}]"));
        when(administrationClient.getDocumentByType(any(), any())).thenReturn(Mono.just(TestData.certificateDto()));
        when(bookingsClient.getAllByStatus(any(), any())).thenReturn(Flux.empty());
        when(schedulesClient.getActiveKitchenSchedules(eq(TestData.USER_ID.toString()))).thenReturn(Flux.empty());
        when(schedulesClient.getActiveResponsibles(TestData.USER_ID)).thenReturn(Flux.empty());

        StepVerifier.create(service.getWholeUser(authentication, ""))
                .assertNext(response -> assertThat(response.id()).isEqualTo(TestData.USER_ID))
                .verifyComplete();
    }

    @Test
    void getWholeUserWhenAllRequestsFailAndEventsEmptyThrowsBadGateway() {
        RuntimeException error = new RuntimeException("недоступен");
        when(userClient.getUserWithRoles(TestData.USER_ID)).thenReturn(Mono.error(error));
        when(administrationClient.getBalanceShort(TestData.USER_ID)).thenReturn(Mono.error(error));
        when(administrationClient.getDocumentByType(TestData.USER_ID, DocumentType.CERTIFICATE))
                .thenReturn(Mono.error(error));
        when(administrationClient.getDocumentByType(TestData.USER_ID, DocumentType.FLUOROGRAPHY))
                .thenReturn(Mono.error(error));
        when(bookingsClient.getAllByStatus(any(), any())).thenReturn(Flux.error(error));
        when(schedulesClient.getActiveKitchenSchedules(anyString())).thenReturn(Flux.error(error));
        when(schedulesClient.getActiveResponsibles(TestData.USER_ID)).thenReturn(Flux.error(error));

        StepVerifier.create(service.getWholeUser(authentication, TestData.USER_ID.toString()))
                .expectError(ServiceException.BadGateway.class)
                .verify();
    }

    @Test
    void getAllUsersWithoutFilters() {
        when(userClient.getAllUsers(eq(0), eq(1000000000), eq(""), eq(""), eq(""), eq("")))
                .thenReturn(Flux.just(TestData.userResponseDto()));
        when(administrationClient.getAllBalances(eq(0), eq(1000000000), eq(null), eq(null)))
                .thenReturn(Flux.just(TestData.balanceResponseDto()));
        when(administrationClient.getAllDocuments(eq(0), eq(1000000000), eq(null), eq(null), eq(null), eq(null)))
                .thenReturn(Flux.just(TestData.certificateDto(), TestData.fluorographyDto()));

        StepVerifier.create(service.getAllUsers(
                        authentication, "0", "1000000000", "", "", "", "",
                        null, null, null, null, null, null))
                .assertNext(dto -> {
                    assertThat(dto.id()).isEqualTo(TestData.USER_ID);
                    assertThat(dto.pediculosis().type()).isEqualTo(DocumentType.CERTIFICATE);
                    assertThat(dto.fluorography().type()).isEqualTo(DocumentType.FLUOROGRAPHY);
                    assertThat(dto.balance()).isEqualByComparingTo(BigDecimal.valueOf(500.0));
                })
                .verifyComplete();
    }

    @Test
    void getAllUsersWithNameFilter() {
        when(userClient.getAllUsers(eq(0), eq(1000), eq(TestData.FIRST_NAME), eq(""), eq(""), eq("")))
                .thenReturn(Flux.just(TestData.userResponseDto()));
        when(administrationClient.getAllBalancesByUsers(any()))
                .thenReturn(Flux.just(TestData.balanceResponseDto()));
        when(administrationClient.getAllDocumentsByUsers(any()))
                .thenReturn(Flux.just(TestData.certificateDto(), TestData.fluorographyDto()));

        StepVerifier.create(service.getAllUsers(
                        authentication, "0", "1000", TestData.FIRST_NAME, "", "", "",
                        null, null, null, null, null, null))
                .assertNext(dto -> assertThat(dto.firstName()).isEqualTo(TestData.FIRST_NAME))
                .verifyComplete();
    }

    @Test
    void getAllUsersWithBalanceFilter() {
        when(administrationClient.getAllBalances(eq(0), eq(1000), eq(true), eq(new BigDecimal("100"))))
                .thenReturn(Flux.just(TestData.balanceResponseDto()));
        when(userClient.getAllUsersWithIds(any())).thenReturn(Flux.just(TestData.userResponseDto()));
        when(administrationClient.getAllDocumentsByUsers(any()))
                .thenReturn(Flux.just(TestData.certificateDto(), TestData.fluorographyDto()));

        StepVerifier.create(service.getAllUsers(
                        authentication, "0", "1000", "", "", "", "",
                        "true", "100", null, null, null, null))
                .assertNext(dto -> assertThat(dto.id()).isEqualTo(TestData.USER_ID))
                .verifyComplete();
    }

    @Test
    void getAllUsersWithCertificateFilter() {
        when(administrationClient.getAllDocuments(anyInt(), anyInt(), any(), any(), any(), any()))
                .thenReturn(Flux.just(TestData.certificateDto(), TestData.fluorographyDto()));
        when(userClient.getAllUsersWithIds(any())).thenReturn(Flux.just(TestData.userResponseDto()));
        when(administrationClient.getAllBalancesByUsers(any()))
                .thenReturn(Flux.just(TestData.balanceResponseDto()));

        StepVerifier.create(service.getAllUsers(
                        authentication, "0", "1000", "", "", "", "",
                        null, null, "true", "2026-01-01", "false", "2026-02-02"))
                .assertNext(dto -> assertThat(dto.id()).isEqualTo(TestData.USER_ID))
                .verifyComplete();
    }

    @Test
    void getAllBookingsWhenTypeIsAll() {
        when(bookingsClient.getAllByDate(TestData.LOCAL_DATE))
                .thenReturn(Flux.just(TestData.activeEventWithUserDto()));
        when(userClient.getAllUsersWithIdsShort(any()))
                .thenReturn(Flux.just(TestData.userShortResponseDto()));

        StepVerifier.create(service.getAllBookings(TestData.BOOKING_TYPE_ALL, TestData.LOCAL_DATE))
                .assertNext(dto -> {
                    assertThat(dto.users()).hasSize(1);
                    assertThat(dto.users().get(0).bookingId()).isEqualTo(TestData.BOOKING_ID);
                })
                .verifyComplete();
    }

    @Test
    void getAllBookingsWhenTypeIsSpecific() {
        when(bookingsClient.getAllByTypeAndDate(TestData.BOOKING_TYPE_GYM, TestData.LOCAL_DATE))
                .thenReturn(Flux.just(TestData.activeEventWithUserDto()));
        when(userClient.getAllUsersWithIdsShort(any()))
                .thenReturn(Flux.just(TestData.userShortResponseDto()));

        StepVerifier.create(service.getAllBookings(TestData.BOOKING_TYPE_GYM, TestData.LOCAL_DATE))
                .assertNext(dto -> assertThat(dto.users()).hasSize(1))
                .verifyComplete();
    }

    @Test
    void getAllBookingsWhenUserClientFails() {
        when(bookingsClient.getAllByDate(TestData.LOCAL_DATE))
                .thenReturn(Flux.just(TestData.activeEventWithUserDto()));
        when(userClient.getAllUsersWithIdsShort(any()))
                .thenReturn(Flux.error(new RuntimeException("недоступен")));

        StepVerifier.create(service.getAllBookings(TestData.BOOKING_TYPE_ALL, TestData.LOCAL_DATE))
                .assertNext(dto -> assertThat(dto.users()).isEmpty())
                .verifyComplete();
    }

    @Test
    void getAllAvailableTimeSlotsSuccess() {
        when(bookingsClient.getAvailableTimeSlots(TestData.LOCAL_DATE, TestData.BOOKING_TYPE_GYM, TestData.USER_ID))
                .thenReturn(Mono.just(TestData.availableTimeSlotsWithResponsible()));
        when(userClient.getUserWithIdShort(TestData.RESPONSIBLE_ID))
                .thenReturn(Mono.just(TestData.responsibleShortResponseDto()));

        StepVerifier.create(service.getAllAvailableTimeSlots(
                        TestData.BOOKING_TYPE_GYM, TestData.LOCAL_DATE, authentication))
                .assertNext(dto -> {
                    assertThat(dto.responsibleFirstName()).isEqualTo(TestData.FIRST_NAME);
                    assertThat(dto.timeSlotResponseDtos()).hasSize(1);
                    assertThat(dto.tgLink()).isEqualTo(TestData.TG_LINK);
                })
                .verifyComplete();
    }
}
