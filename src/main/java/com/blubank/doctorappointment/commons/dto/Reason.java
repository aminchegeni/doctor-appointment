package com.blubank.doctorappointment.commons.dto;

import lombok.Getter;

@Getter
public enum Reason {

    UNKNOWN         (-1),
    INVALID         (0),
    MISSED          (1),
    NOT_EXISTENT    (1);

    private final int code;

    Reason(int code) {
        this.code = code;
    }
}
