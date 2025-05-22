package ru.tpu.hostel.api_gateway.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public enum Microservice {

    USER("Сервис пользователей", "user-service", List.of("users", "roles", "sessions"), "userservice", "8081"),
    BOOKING("Сервис бронирования", "booking-service", List.of("bookings"), "bookingservice", "8082"),
    SCHEDULE("Сервис расписаний", "schedule-service", List.of("schedules", "responsibles"), "schedulesservice", "8084"),
    ADMINISTRATION("Сервис администрации", "admin-service", List.of("documents", "balance"), "administrationservice", "8083"),
    NOTIFICATION("Сервис уведомлений", "notification-service", List.of("notifications"), "notificationservice", "8085");

    private static final Microservice[] VALUES = values();

    private static final String UNKNOWN = "Сервис";

    @Getter
    private final String serviceName;

    @Getter
    private final String additionalName;

    private final List<String> paths;

    private final String kubernetesName;

    private final String localPort;

    public static String getMicroserviceAdditionalName(String path) {
        for (Microservice value : VALUES) {
            if (value.paths.contains(path)) {
                return value.additionalName;
            }
        }

        return UNKNOWN;
    }

    public static String getMicroserviceNameInKubernetes(String kubernetesName) {
        for (Microservice value : VALUES) {
            if (value.kubernetesName.equals(kubernetesName)) {
                return value.serviceName;
            }
        }

        return UNKNOWN;
    }

    public static String getMicroserviceNameLocal(String localPort) {
        for (Microservice value : VALUES) {
            if (value.localPort.equals(localPort)) {
                return value.serviceName;
            }
        }

        return UNKNOWN;
    }
}
