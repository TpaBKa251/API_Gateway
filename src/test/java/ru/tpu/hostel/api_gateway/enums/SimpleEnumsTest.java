package ru.tpu.hostel.api_gateway.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleEnumsTest {

    @Test
    void bookingStatusNames() {
        assertThat(BookingStatus.NOT_BOOKED.getBookingStatusName()).isEqualTo("Не забронировано");
        assertThat(BookingStatus.BOOKED.getBookingStatusName()).isEqualTo("Предстоящее");
        assertThat(BookingStatus.IN_PROGRESS.getBookingStatusName()).isEqualTo("В процессе");
        assertThat(BookingStatus.CANCELLED.getBookingStatusName()).isEqualTo("Отменено");
        assertThat(BookingStatus.COMPLETED.getBookingStatusName()).isEqualTo("Завершено");
    }

    @Test
    void documentTypeNames() {
        assertThat(DocumentType.CERTIFICATE.getDocumentName()).isEqualTo("Справка на чесотку и педикулез");
        assertThat(DocumentType.FLUOROGRAPHY.getDocumentName()).isEqualTo("Флюорография");
    }

    @Test
    void eventTypeNames() {
        assertThat(EventType.HALL.getEventTypeName()).isEqualTo("Зал");
        assertThat(EventType.INTERNET.getEventTypeName()).isEqualTo("Интернет");
        assertThat(EventType.GYM.getEventTypeName()).isEqualTo("Тренажерный зал");
        assertThat(EventType.KITCHEN.getEventTypeName()).isEqualTo("Кухня");
        assertThat(EventType.SOOP.getEventTypeName()).isEqualTo("СООП");
    }
}
