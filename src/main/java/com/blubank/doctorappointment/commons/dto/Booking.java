package com.blubank.doctorappointment.commons.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Booking implements Serializable {

    @Serial
    private static final long serialVersionUID = 4732355988544962950L;

    @NotNull
    @Pattern(regexp = "^R[0-9A-F]{16}$")
    private String ref;

    @NotNull
    @Size(min = 3, max = 100)
    private String name;

    @NotNull
    @Pattern(regexp = "^[0-9]{11}$")
    private String phone;
}
