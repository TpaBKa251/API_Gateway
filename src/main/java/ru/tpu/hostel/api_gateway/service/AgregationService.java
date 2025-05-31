package ru.tpu.hostel.api_gateway.service;

import org.springframework.security.core.Authentication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.tpu.hostel.api_gateway.dto.AdminResponseDto;
import ru.tpu.hostel.api_gateway.dto.AvailableTimeSlotsDto;
import ru.tpu.hostel.api_gateway.dto.BookingResponseWithUsersDto;
import ru.tpu.hostel.api_gateway.dto.WholeUserResponseDto;

public interface AgregationService {

    Mono<WholeUserResponseDto> getWholeUser(Authentication authentication, String roomFromRequest);

    Flux<AdminResponseDto> getAllUsers(
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
    );

    Flux<BookingResponseWithUsersDto> getAllBookings(String type, String localDate);

    Mono<AvailableTimeSlotsDto> getAllAvailableTimeSlots(String type, String localDate, Authentication authentication);
}
