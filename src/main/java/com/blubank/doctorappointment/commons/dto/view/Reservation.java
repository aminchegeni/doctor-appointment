package com.blubank.doctorappointment.commons.dto.view;

import com.blubank.doctorappointment.commons.model.Status;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Reservation(
        LocalDate day,
        String ref,
        LocalTime start,
        int duration, // in minuets
        Status status,
        String name,
        String phone
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 8299349761344562299L;
}
