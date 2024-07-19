package com.blubank.doctorappointment.commons.dto;

import lombok.Getter;

@Getter
public enum Outcome {

    UNKNOWN (-1),
    SUCCESS (0),
    FAILED  (1);

    private final int code;

    Outcome(int code) {
        this.code = code;
    }
}
