package com.blubank.doctorappointment.api;

import com.blubank.doctorappointment.commons.dto.ActivePeriod;
import com.blubank.doctorappointment.commons.dto.Booking;
import com.blubank.doctorappointment.commons.dto.DailySchedule;
import com.blubank.doctorappointment.commons.dto.Error;
import com.blubank.doctorappointment.commons.dto.view.Reservation;
import com.blubank.doctorappointment.commons.exceptions.DoseNotExistException;
import com.blubank.doctorappointment.commons.exceptions.HasBeenTakenException;
import com.blubank.doctorappointment.commons.model.Appointment;
import com.blubank.doctorappointment.commons.model.Status;
import com.hazelcast.flakeidgen.FlakeIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static com.blubank.doctorappointment.commons.dto.Reason.MISSED;
import static com.blubank.doctorappointment.commons.dto.Reason.NOT_EXISTENT;
import static com.blubank.doctorappointment.commons.model.Status.*;

@Service
public class AppointmentManager {

    @Value("${ir.monshino.appointment.length:30}")
    private int appointmentLength;

    private final AppointmentRepository repository;
    private final StatusesAdapter adapter;
    private final FlakeIdGenerator seq;

    @Autowired
    public AppointmentManager(AppointmentRepository repository, StatusesAdapter adapter, FlakeIdGenerator seq) {
        this.repository = repository;
        this.adapter = adapter;
        this.seq = seq;
    }

    public long doPlaning(DailySchedule schedule) {
        LocalDate day = schedule.getDay();
        ActivePeriod period = schedule.getPeriod();
        LocalTime start = period.getStart();
        long count = Duration.between(period.getStart(), period.getEnd()).toMinutes() / appointmentLength;
        repository.saveAll(
                IntStream.iterate(0, i -> i + 1)
                        .limit(count)
                        .mapToObj(i ->
                                Appointment.builder()
                                        .day(day)
                                        .ref("R%016X".formatted(seq.newId()))
                                        .start(start.plusMinutes((long) i * appointmentLength))
                                        .duration(appointmentLength)
                                        .status(OPEN)
                                        .build()
                        )
                        .toList()
        );
        return count;
    }

    public boolean isTaken(String ref) {
        return repository.existsByRefAndStatus(ref, TAKEN);
    }

    public boolean isPlaned(LocalDate day) {
        return repository.existsByDayAndStatusIn(day, CANCELED.negate());
    }

    public void removeAppointment(String ref) {
        int numberOfAffected = repository.updateStatusByRefAndStatus(
                Criteria.builder()
                        .values(
                                Map.of("status", CANCELED)
                        )
                        .wheres(
                                Map.of(
                                        "ref", ref,
                                        "status", OPEN
                                )
                        )
                        .build()
        );
        throwExceptionIfRequired(numberOfAffected, ref);
    }

    public List<Reservation> getAppointments(LocalDate day, String state) {
        EnumSet<Status> statuses = adapter.convert(state);
        return repository.findByDayAndStatusIn(day, statuses);
    }

    public List<Reservation> getAppointments(String phone) {
        return repository.findByPhone(phone);
    }

    public Reservation reserveAppointment(Booking booking) {
        var ref = booking.getRef();
        var name = booking.getName();
        var phone = booking.getPhone();
        int numberOfAffected = repository.updateStatusAndNameAndPhoneByRefAndStatus(
                Criteria.builder()
                        .values(
                                Map.of(
                                        "status", TAKEN,
                                        "name", name,
                                        "phone", phone
                                )
                        )
                        .wheres(
                                Map.of(
                                        "ref", ref,
                                        "status", OPEN
                                )
                        )
                        .build()
        );
        throwExceptionIfRequired(numberOfAffected, ref);
        return repository.findByRef(ref);
    }

    private void throwExceptionIfRequired(int numberOfAffected, String ref) {
        if (numberOfAffected < 1) {
            if (isTaken(ref)) {
                throw new HasBeenTakenException(
                        Error.builder()
                                .reason(MISSED)
                                .param("ref")
                                .description("requested appointment has been taken")
                                .values(new String[]{ref})
                                .build()
                );
            } else {
                throw new DoseNotExistException(
                        Error.builder()
                                .reason(NOT_EXISTENT)
                                .param("ref")
                                .description("requested appointment dose not exist")
                                .values(new String[]{ref})
                                .build()
                );
            }
        }
    }
}
