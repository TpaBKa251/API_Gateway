package ru.tpu.hostel.api_gateway.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum BookingStatus {
    NOT_BOOKED("Не забронировано"),
    BOOKED("Забронировано"),
    IN_PROGRESS("В процессе"),
    CANCELLED("Отменено"),
    COMPLETED("Завершено");

    private final String bookingStatusName;
}