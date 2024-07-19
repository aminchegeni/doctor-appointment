package com.blubank.doctorappointment.commons.dto;

import com.blubank.doctorappointment.commons.constraint.NotYetPlaned;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailySchedule implements Serializable {

    @Serial
    private static final long serialVersionUID = -3196716424989874350L;

    @NotNull
    @FutureOrPresent
    @NotYetPlaned
    private LocalDate day;

    @Valid
    @NotNull
    @JsonUnwrapped
    private ActivePeriod period;
}
