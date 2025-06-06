package ru.tpu.hostel.api_gateway.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EventType {
    HALL("Зал"),
    INTERNET("Интернет"),
    GYM("Тренажерный зал"),
    KITCHEN("Кухня"),
    SOOP("СООП");

    private final String eventTypeName;
}