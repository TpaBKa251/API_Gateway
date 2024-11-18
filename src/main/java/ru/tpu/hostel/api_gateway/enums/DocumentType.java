package ru.tpu.hostel.api_gateway.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum DocumentType {
    CERTIFICATE("Справка на чесотку и педикулез"),
    FLUOROGRAPHY("Флюорография");

    private final String documentName;
}