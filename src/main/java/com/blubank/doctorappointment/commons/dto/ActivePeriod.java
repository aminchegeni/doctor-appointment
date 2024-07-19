package com.blubank.doctorappointment.commons.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@GroupSequence({ActivePeriod.Inner.class, ActivePeriod.class})
@Positive(message = "end time is sooner than start time")
public class ActivePeriod implements Serializable {

    public interface Inner {}

    @Serial
    private static final long serialVersionUID = -3766297563309186715L;

    @NotNull(groups = Inner.class)
    private LocalTime start;

    @NotNull(groups = Inner.class)
    private LocalTime end;
}
