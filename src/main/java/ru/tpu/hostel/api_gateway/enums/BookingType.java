package ru.tpu.hostel.api_gateway.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum BookingType {
    HALL("Зал", 0),
    INTERNET("Интернет", 1),
    GYM("Тренажерный зал", 10);

    private final String bookingTypeName;
    private final Integer limit;
}
