package com.blubank.doctorappointment.utils;

import com.blubank.doctorappointment.commons.dto.Error;
import com.blubank.doctorappointment.commons.dto.*;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.stream.Stream;

import static com.blubank.doctorappointment.commons.dto.Outcome.FAILED;
import static com.blubank.doctorappointment.commons.dto.Outcome.SUCCESS;
import static com.blubank.doctorappointment.commons.dto.Reason.MISSED;
import static com.blubank.doctorappointment.commons.dto.Reason.NOT_EXISTENT;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class TestUtils {

    private TestUtils() {
        throw new AssertionError("Utility class");
    }

    public static <T> T getResult(Future<T> f) {
        try {
            return f.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> ParameterizedTypeReference<ApiResp<T>> parameterized(Class<T> clazz) {
        return ParameterizedTypeReference.forType(ResolvableType.forClassWithGenerics(ApiResp.class, clazz).getType());
    }

    public static <T> ParameterizedTypeReference<ApiResp<T>> parameterized(ResolvableType type) {
        return ParameterizedTypeReference.forType(ResolvableType.forClassWithGenerics(ApiResp.class, type).getType());
    }

    public static <T> Set<Error> assertFailedRespAndExtractErrors(ResponseEntity<ApiResp<T>> resp) {
        assertTrue(resp.hasBody());
        var body = resp.getBody();
        assertNotNull(body);
        assertSame(FAILED, body.getOutcome());
        assertNull(body.getResult());
        var errors = body.getErrors();
        assertNotNull(errors);
        assertFalse(errors.isEmpty());
        return errors;
    }

    public static <T> T assertSuccessRespAndExtractResult(ResponseEntity<ApiResp<T>> resp) {
        assertTrue(resp.hasBody());
        var body = resp.getBody();
        assertNotNull(body);
        assertSame(SUCCESS, body.getOutcome());
        var errors = body.getErrors();
        assertNotNull(errors);
        assertTrue(errors.isEmpty());
        var result = body.getResult();
        assertNotNull(result);
        return result;
    }

    public static <T> void assertMissedAppointment(ResponseEntity<ApiResp<T>> resp) {
        assertUnavailableAppointment(resp, MISSED, "requested appointment has been taken");
    }

    public static <T> void assertNotExistAppointment(ResponseEntity<ApiResp<T>> resp) {
        assertUnavailableAppointment(resp, NOT_EXISTENT, "requested appointment dose not exist");
    }

    private static <T> void assertUnavailableAppointment(ResponseEntity<ApiResp<T>> resp, Reason reason, String des) {
        var errors = assertFailedRespAndExtractErrors(resp);
        assertEquals(1, errors.size());
        assertThat(errors.iterator().next()).isNotNull()
                .hasFieldOrPropertyWithValue("reason", reason)
                .hasFieldOrPropertyWithValue("param", "ref")
                .hasFieldOrPropertyWithValue("description", des);
    }

    public static Stream<Arguments> generateInvalidDailySchedules() {
        var period = ActivePeriod.builder()
                .start(LocalTime.now())
                .end(LocalTime.now().plusHours(1L))
                .build();
        return Stream.of(
                arguments(DailySchedule.builder()
                        .period(period)
                        .build()), // null day
                arguments(DailySchedule.builder()
                        .day(LocalDate.now().minusDays(1L))
                        .period(period)
                        .build()), // past day
                arguments(DailySchedule.builder()
                        .day(LocalDate.now())
                        .period(ActivePeriod.builder().end(LocalTime.now()).build())
                        .build()), // null start time
                arguments(DailySchedule.builder()
                        .day(LocalDate.now())
                        .period(ActivePeriod.builder().start(LocalTime.now()).build())
                        .build()), // null end time
                arguments(DailySchedule.builder()
                        .day(LocalDate.now())
                        .period(ActivePeriod.builder()
                                .start(LocalTime.now())
                                .end(LocalTime.now().minusHours(1L)).build())
                        .build())  // end time sooner than start time
        );
    }

    public static Stream<Arguments> generateInvalidBooking() {
        return Stream.of(
                arguments(Booking.builder()
                        .ref(null)
                        .name("Amin")
                        .phone("09163412114")
                        .build()), // null ref
                arguments(Booking.builder()
                        .ref("R01234")
                        .name("Amin")
                        .phone("09163412114")
                        .build()), // invalid pattern ref
                arguments(Booking.builder()
                        .ref("R0000000000000000123456789")
                        .name("Amin")
                        .phone("09163412114")
                        .build()), // invalid pattern ref
                arguments(Booking.builder()
                        .ref("R0000000000000XYZ")
                        .name("Amin")
                        .phone("09163412114")
                        .build()), // invalid pattern ref
                arguments(Booking.builder()
                        .ref("R0000000000000000")
                        .name(null)
                        .phone("09163412114")
                        .build()), // null name
                arguments(Booking.builder()
                        .ref("R0000000000000000")
                        .name("AM")
                        .phone("09163412114")
                        .build()), // invalid length name
                arguments(Booking.builder()
                        .ref("R0000000000000000")
                        .name("Amin")
                        .phone(null)
                        .build()), // null phone
                arguments(Booking.builder()
                        .ref("R0000000000000000")
                        .name("Amin")
                        .phone("0916AMIN114")
                        .build()), // invalid pattern phone
                arguments(Booking.builder()
                        .ref("R0000000000000000")
                        .name("Amin")
                        .phone("09163412")
                        .build()), // invalid pattern phone
                arguments(Booking.builder()
                        .ref("R0000000000000000")
                        .name("Amin")
                        .phone("091634121140000")
                        .build()) // invalid pattern phone
        );
    }
}
