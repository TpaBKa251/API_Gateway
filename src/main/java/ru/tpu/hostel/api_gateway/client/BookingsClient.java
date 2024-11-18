package ru.tpu.hostel.api_gateway.client;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.tpu.hostel.api_gateway.dto_library.request.BookingTimeLineRequestDto;
import ru.tpu.hostel.api_gateway.dto_library.request.BookingTimeSlotRequestDto;
import ru.tpu.hostel.api_gateway.dto_library.response.BookingResponseDto;
import ru.tpu.hostel.api_gateway.dto_library.response.BookingShortResponseDto;
import ru.tpu.hostel.api_gateway.dto_library.response.TimeSlotResponseDto;
import ru.tpu.hostel.api_gateway.enums.BookingStatus;
import ru.tpu.hostel.api_gateway.enums.BookingType;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Component
@FeignClient(name = "booking-bookingservice", url = "http://bookingservice:8080")
public interface BookingsClient {

    @PostMapping("/timeline/{userId}")
    BookingResponseDto bookTimeline(
            @RequestBody BookingTimeLineRequestDto bookingTimeLineRequestDto,
            @PathVariable UUID userId
    );

    @PostMapping("/timeslot/{userId}")
    BookingResponseDto bookTimeSlot(
            @RequestBody BookingTimeSlotRequestDto bookingTimeSlotRequestDto,
            @PathVariable UUID userId
    );

    @GetMapping("/available/timeline/{date}/{bookingType}")
    List<BookingShortResponseDto> getAvailableTimeBookings(
            @PathVariable LocalDate date,
            @PathVariable BookingType bookingType
    );

    @GetMapping("/available/timeslot/{date}/{bookingType}")
    List<TimeSlotResponseDto> getAvailableTimeBooking(
            @PathVariable LocalDate date,
            @PathVariable BookingType bookingType
    );

    @PatchMapping("/cancel/{bookingId}/{userId}")
    BookingResponseDto cancelBooking(
            @PathVariable UUID bookingId,
            @PathVariable UUID userId
    );

    @GetMapping("/get/all/{status}/{userId}")
    List<BookingResponseDto> getAllByStatus(
            @PathVariable BookingStatus status,
            @PathVariable UUID userId
    );

    @GetMapping("/get/all/{userId}")
    List<BookingResponseDto> getAllByUserId(
            @PathVariable UUID userId
    );
}

