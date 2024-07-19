package com.blubank.doctorappointment.commons.model;

import lombok.Getter;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum Status {

    UNKNOWN     ('U'),
    OPEN        ('O'),
    TAKEN       ('T'),
    CANCELED    ('C');

    private static final Map<Character, Status> CODE_TO_STATUS_MAP = Stream.of(Status.class.getEnumConstants())
            .collect(Collectors.collectingAndThen(
                    Collectors.toMap(
                            Status::getCode,
                            Function.identity()),
                    Collections::unmodifiableMap));

    private final char code;

    Status(char code) {
        this.code = code;
    }

    public EnumSet<Status> negate() {
        return EnumSet.complementOf(EnumSet.of(this));
    }

    public static Status valueOf(char code) {
        return CODE_TO_STATUS_MAP.getOrDefault(code, UNKNOWN);
    }
}
