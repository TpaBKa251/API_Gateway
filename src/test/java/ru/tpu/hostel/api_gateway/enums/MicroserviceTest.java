package ru.tpu.hostel.api_gateway.enums;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class MicroserviceTest {

    @ParameterizedTest
    @CsvSource({
            "users, user-service",
            "roles, user-service",
            "sessions, user-service",
            "bookings, booking-service",
            "schedules, schedule-service",
            "responsibles, schedule-service",
            "documents, admin-service",
            "balance, admin-service",
            "notifications, notification-service"
    })
    void getMicroserviceAdditionalNameWhenPathIsKnown(String path, String expected) {
        assertThat(Microservice.getMicroserviceAdditionalName(path)).isEqualTo(expected);
    }

    @Test
    void getMicroserviceAdditionalNameWhenPathIsUnknown() {
        assertThat(Microservice.getMicroserviceAdditionalName("unknown")).isEqualTo("Сервис");
    }

    @ParameterizedTest
    @CsvSource({
            "userservice, Сервис пользователей",
            "bookingservice, Сервис бронирования",
            "schedulesservice, Сервис расписаний",
            "administrationservice, Сервис администрации",
            "notificationservice, Сервис уведомлений"
    })
    void getMicroserviceNameInKubernetesWhenNameIsKnown(String kubernetesName, String expected) {
        assertThat(Microservice.getMicroserviceNameInKubernetes(kubernetesName)).isEqualTo(expected);
    }

    @Test
    void getMicroserviceNameInKubernetesWhenNameIsUnknown() {
        assertThat(Microservice.getMicroserviceNameInKubernetes("unknown")).isEqualTo("Сервис");
    }

    @ParameterizedTest
    @CsvSource({
            "8081, Сервис пользователей",
            "8082, Сервис бронирования",
            "8084, Сервис расписаний",
            "8083, Сервис администрации",
            "8085, Сервис уведомлений"
    })
    void getMicroserviceNameLocalWhenPortIsKnown(String port, String expected) {
        assertThat(Microservice.getMicroserviceNameLocal(port)).isEqualTo(expected);
    }

    @Test
    void getMicroserviceNameLocalWhenPortIsUnknown() {
        assertThat(Microservice.getMicroserviceNameLocal("9999")).isEqualTo("Сервис");
    }

    @Test
    void getServiceNameAndAdditionalName() {
        assertThat(Microservice.USER.getServiceName()).isEqualTo("Сервис пользователей");
        assertThat(Microservice.USER.getAdditionalName()).isEqualTo("user-service");
    }
}
