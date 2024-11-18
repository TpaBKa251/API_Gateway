package ru.tpu.hostel.api_gateway.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tpu.hostel.api_gateway.client.BookingsClient;
import ru.tpu.hostel.api_gateway.dto_library.request.BookingTimeLineRequestDto;
import ru.tpu.hostel.api_gateway.dto_library.request.BookingTimeSlotRequestDto;
import ru.tpu.hostel.api_gateway.dto_library.response.BookingResponseDto;
import ru.tpu.hostel.api_gateway.dto_library.response.BookingShortResponseDto;
import ru.tpu.hostel.api_gateway.dto_library.response.TimeSlotResponseDto;
import ru.tpu.hostel.api_gateway.enums.BookingStatus;
import ru.tpu.hostel.api_gateway.enums.BookingType;
import ru.tpu.hostel.api_gateway.filter.JwtAuthenticationFilter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class BookingsController {

    private final BookingsClient bookingsClient;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // Бронирование через временную шкалу
    @PostMapping("/timeline")
    public BookingResponseDto bookTimeline(
            @RequestBody @Valid BookingTimeLineRequestDto bookingTimeLineRequestDto,
            Authentication authentication
    ) {
        UUID userId = jwtAuthenticationFilter.getUserIdFromToken(authentication);
        return bookingsClient.bookTimeline(bookingTimeLineRequestDto, userId);
    }

    // Бронирование через слот времени
    @PostMapping("/timeslot")
    public BookingResponseDto bookTimeSlot(
            @RequestBody @Valid BookingTimeSlotRequestDto bookingTimeSlotRequestDto,
            Authentication authentication
    ) {
        UUID userId = jwtAuthenticationFilter.getUserIdFromToken(authentication);
        return bookingsClient.bookTimeSlot(bookingTimeSlotRequestDto, userId);
    }

    // Получение доступных бронирований по временной шкале
    @GetMapping("/available/timeline/{date}/{bookingType}")
    public List<BookingShortResponseDto> getAvailableTimeBookings(
            @PathVariable LocalDate date,
            @PathVariable BookingType bookingType
    ) {
        return bookingsClient.getAvailableTimeBookings(date, bookingType);
    }

    // Получение доступных бронирований по временному слоту
    @GetMapping("/available/timeslot/{date}/{bookingType}")
    public List<TimeSlotResponseDto> getAvailableTimeBooking(
            @PathVariable LocalDate date,
            @PathVariable BookingType bookingType
    ) {
        return bookingsClient.getAvailableTimeBooking(date, bookingType);
    }

    // Отмена бронирования
    @PatchMapping("/cancel/{bookingId}")
    public BookingResponseDto cancelBooking(
            @PathVariable UUID bookingId,
            Authentication authentication
    ) {
        UUID userId = jwtAuthenticationFilter.getUserIdFromToken(authentication);
        return bookingsClient.cancelBooking(bookingId, userId);
    }

    // Получение всех бронирований по статусу
    @GetMapping("/get/all/{status}")
    public List<BookingResponseDto> getAllByStatus(
            @PathVariable BookingStatus status,
            Authentication authentication
    ) {
        UUID userId = jwtAuthenticationFilter.getUserIdFromToken(authentication);
        return bookingsClient.getAllByStatus(status, userId);
    }

    // Получение всех бронирований для пользователя
    @GetMapping("/get/all")
    public List<BookingResponseDto> getAllByUserId(Authentication authentication) {
        UUID userId = jwtAuthenticationFilter.getUserIdFromToken(authentication);
        return bookingsClient.getAllByUserId(userId);
    }
}
