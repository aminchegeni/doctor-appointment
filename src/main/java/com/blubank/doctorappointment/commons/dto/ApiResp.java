package com.blubank.doctorappointment.commons.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

import static com.blubank.doctorappointment.commons.dto.Outcome.*;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static java.util.Objects.requireNonNullElseGet;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(NON_NULL)
public class ApiResp<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = -1602535463064962419L;

    private Outcome outcome = UNKNOWN;

    private T result;

    private Set<Error> errors = Collections.emptySet();

    private String meta = "";

    public static <T> ApiResp<T> createSuccessResp(T result) {
        var resp = new ApiResp<T>();
        resp.setResult(result);
        resp.setOutcome(SUCCESS);
        return resp;
    }

    public static ApiResp<?> createFailedResp(Set<Error> errors) {
        var resp = new ApiResp<>();
        resp.setErrors(requireNonNullElseGet(errors, Collections::emptySet));
        resp.setOutcome(FAILED);
        return resp;
    }
}
