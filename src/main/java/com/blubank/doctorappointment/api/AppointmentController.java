package com.blubank.doctorappointment.api;

import com.blubank.doctorappointment.commons.dto.ApiResp;
import com.blubank.doctorappointment.commons.dto.Booking;
import com.blubank.doctorappointment.commons.dto.DailySchedule;
import com.blubank.doctorappointment.commons.dto.view.Reservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.List;

import static java.util.Objects.requireNonNullElseGet;
import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Validated
@Controller
@RequestMapping(
        path = "/v1/appointments",
        produces = APPLICATION_JSON_VALUE,
        consumes = APPLICATION_JSON_VALUE
)
@ResponseBody
public class AppointmentController {

    private final AppointmentManager manager;

    @Autowired
    public AppointmentController(AppointmentManager manager) {
        this.manager = manager;
    }

    @PreAuthorize("hasAuthority('DOCTOR')")
    @PostMapping
    public ApiResp<Long> addAppointments(@Valid @RequestBody DailySchedule schedule) {
        return ApiResp.createSuccessResp(manager.doPlaning(schedule));
    }

    @PreAuthorize("hasAuthority('DOCTOR')")
    @DeleteMapping(
            consumes = ALL_VALUE
    )
    @ResponseStatus(NO_CONTENT)
    public void removeAppointment(@Pattern(regexp = "^R[0-9A-F]{16}$") @RequestParam(value = "ref") String ref) {
        manager.removeAppointment(ref);
    }

    @PreAuthorize("""
            (#state.endsWith('canceled') && hasAuthority('DOCTOR'))
                or (#state == 'open' && hasAuthority('PATIENT'))
            """)
    @GetMapping(
            value = "/{state:^!?canceled|open$}",
            consumes = ALL_VALUE
    )
    public ApiResp<List<Reservation>> getAppointments(
            @RequestParam(value = "day", required = false) @DateTimeFormat(iso = DATE) LocalDate day,
            @PathVariable("state") String state) {
        day = requireNonNullElseGet(day, LocalDate::now);
        return ApiResp.createSuccessResp(manager.getAppointments(day, state));
    }

    @PreAuthorize("hasAuthority('PATIENT')")
    @PatchMapping
    public ApiResp<Reservation> bookAppointment(@Valid @RequestBody Booking booking) {
        return ApiResp.createSuccessResp(manager.reserveAppointment(booking));
    }

    @PreAuthorize("hasAuthority('PATIENT')")
    @GetMapping(
            value = "/{phone:^[0-9]{11}$}",
            consumes = ALL_VALUE
    )
    public ApiResp<List<Reservation>> getAppointments(@PathVariable("phone") String phone) {
        return ApiResp.createSuccessResp(manager.getAppointments(phone));
    }
}
