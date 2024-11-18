package ru.tpu.hostel.api_gateway.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Roles {
    STUDENT("Студент"),
    FLOOR_SUPERVISOR("Староста этажа"),
    HOSTEL_SUPERVISOR("Староста общежития"),
    ADMINISTRATION("Администрация"),
    RESPONSIBLE_KITCHEN("Ответственный за кухню"),
    RESPONSIBLE_HALL("Ответственный за зал"),
    RESPONSIBLE_FIRE_SAFETY("Ответственный за пожарную безопасность"),
    RESPONSIBLE_SANITARY("Ответственный за санитарную проверку"),
    RESPONSIBLE_INTERNET("Ответственный за подключение к Интернету"),
    RESPONSIBLE_SOOP("Ответственный за СООП"),
    WORKER_FIRE_SAFETY("Работник пожарной безопасности"),
    WORKER_SANITARY("Работник санитарной проверки"),
    WORKER_SOOP("Работник СООП");

    private final String roleName;
}

