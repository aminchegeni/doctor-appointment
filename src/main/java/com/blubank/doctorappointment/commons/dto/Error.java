package com.blubank.doctorappointment.commons.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Comparator;

import static com.blubank.doctorappointment.commons.dto.Reason.UNKNOWN;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(NON_NULL)
public class Error implements Serializable, Comparable<Error> {

    @Serial
    private static final long serialVersionUID = -3045731012099931205L;

    private Reason reason = UNKNOWN;

    private String param;

    private String description = "";

    private String[] values = new String[0];

    @Override
    public int compareTo(Error o) {
        return Comparator.comparing(Error::getReason)
                .thenComparing(Error::getParam)
                .thenComparing(Error::getDescription)
                .compare(this, o);
    }
}
