package com.blubank.doctorappointment;

import com.blubank.doctorappointment.commons.dto.ActivePeriod;
import com.blubank.doctorappointment.commons.dto.ApiResp;
import com.blubank.doctorappointment.commons.dto.Booking;
import com.blubank.doctorappointment.commons.dto.DailySchedule;
import com.blubank.doctorappointment.commons.dto.view.Reservation;
import com.blubank.doctorappointment.utils.TestUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.blubank.doctorappointment.commons.model.Status.OPEN;
import static com.blubank.doctorappointment.commons.model.Status.TAKEN;
import static com.blubank.doctorappointment.utils.TestUtils.*;
import static java.util.stream.Collectors.groupingBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.SqlMergeMode.MergeMode.MERGE;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Sql(statements = "DELETE FROM appointment WHERE id > 0", executionPhase = AFTER_TEST_METHOD)
@SqlMergeMode(MERGE)
class DoctorAppointmentApplicationTests {

    @TestConfiguration
    public static class ClientAuthConfig {

        @Bean
        public RestTemplateBuilder buildRestTemplateBuilder() {
            return new RestTemplateBuilder().basicAuthentication("admin", "admin");
        }
    }

    private static final String SERVICE_PATH = "/v1/appointments";

    private static ExecutorService executor;

    @Value("${ir.monshino.appointment.length:30}")
    private int appointmentLength;

    @Autowired
    private TestRestTemplate client;

    @BeforeAll
    static void beforeAll() {
        executor = Executors.newFixedThreadPool(4);
    }

    @ParameterizedTest
    @MethodSource("com.blubank.doctorappointment.utils.TestUtils#generateInvalidDailySchedules")
    void test_failed_schedule_appointments_when_request_value_is_invalid(DailySchedule schedule) {
        doPlaning(schedule);
    }

    // assignment -> section -> 1.1
    @Test
    @Sql("test_schedule_appointments_failed_when_day_has_been_planned.sql")
    void test_failed_schedule_appointments_when_day_has_been_planned() {
        var schedule = DailySchedule.builder()
                .day(LocalDate.now())
                .period(ActivePeriod.builder()
                        .start(LocalTime.now())
                        .end(LocalTime.now().plusHours(1L))
                        .build())
                .build();

        doPlaning(schedule);
    }

    // assignment -> section -> 1.2
    @ParameterizedTest
    @ValueSource(longs = {1L, 2L, 3L, 4L, 5L, 7L, 8L, 9L, 10L})
    void test_success_schedule_appointments_when_day_has_been_planned(long offset) {
        var day = LocalDate.now().plusDays(offset);
        var start = LocalTime.now();
        var end = start.plusMinutes(offset * appointmentLength);
        assumeTrue(end.isAfter(start));
        var schedule = DailySchedule.builder()
                .day(day)
                .period(ActivePeriod.builder()
                        .start(start)
                        .end(end)
                        .build())
                .build();
        var resp = client.exchange(SERVICE_PATH, POST, new HttpEntity<>(schedule), parameterized(Long.class));

        assertSame(OK, resp.getStatusCode());
        var result = assertSuccessRespAndExtractResult(resp);
        assertEquals(offset, result);
    }

    // assignment -> section -> 2.1
    // assignment -> section -> 4.1
    @ParameterizedTest
    @ValueSource(strings = {"!canceled", "open"})
    @Sql("test_success_get_appointments_when_non_canceled_record_dose_not_exist.sql")
    void test_success_get_appointments_when_non_canceled_records_do_not_exist(String state) {
        var type = ResolvableType.forClassWithGenerics(List.class, Reservation.class);
        var url = SERVICE_PATH + "/{state}?day={day}";
        var resp = client.exchange(
                url, GET, HttpEntity.EMPTY, TestUtils.<List<Reservation>>parameterized(type), state, LocalDate.now());

        assertSame(OK, resp.getStatusCode());
        var result = assertSuccessRespAndExtractResult(resp);
        assertTrue(result.isEmpty());
    }

    // assignment -> section -> 2.2
    @Test
    @Sql("test_success_get_appointments_when_there_are_some_open_records.sql")
    void test_success_get_appointments_when_there_are_some_open_records() {
        var type = ResolvableType.forClassWithGenerics(List.class, Reservation.class);
        var url = SERVICE_PATH + "/!canceled?day={day}";
        var resp = client.exchange(
                url, GET, HttpEntity.EMPTY, TestUtils.<List<Reservation>>parameterized(type), LocalDate.now());

        assertSame(OK, resp.getStatusCode());
        var result = assertSuccessRespAndExtractResult(resp);
        assertEquals(2, result.size());
        assertThat(result.get(0)).isNotNull()
                .hasNoNullFieldsOrPropertiesExcept("name", "phone")
                .hasFieldOrPropertyWithValue("status", OPEN);
        assertThat(result.get(1)).isNotNull()
                .hasNoNullFieldsOrProperties()
                .hasFieldOrPropertyWithValue("status", TAKEN);
    }

    // assignment -> section -> 3.1
    @Test
    @Sql("test_failed_remove_appointment_when_it_is_not_open.sql")
    void test_failed_remove_appointment_when_it_is_not_open() {
        var url = SERVICE_PATH + "?ref={ref}";
        var resp = client.exchange(
                url, DELETE, HttpEntity.EMPTY, parameterized(Reservation.class), "R0000000000000000");

        assertSame(NOT_FOUND, resp.getStatusCode());
        assertNotExistAppointment(resp);
    }

    // assignment -> section -> 3.2
    @Test
    @Sql("test_failed_remove_appointment_when_it_has_been_taken.sql")
    void test_failed_remove_appointment_when_it_has_been_taken() {
        var url = SERVICE_PATH + "?ref={ref}";
        var resp = client.exchange(
                url, DELETE, HttpEntity.EMPTY, parameterized(Reservation.class), "R0000000000000000");

        assertSame(NOT_ACCEPTABLE, resp.getStatusCode());
        assertMissedAppointment(resp);
    }

    @Test
    @Sql("test_success_remove_appointment.sql")
    void test_success_remove_appointment() {
        var url = SERVICE_PATH + "?ref={ref}";
        var resp = client.exchange(
                url, DELETE, HttpEntity.EMPTY, parameterized(Reservation.class), "R0000000000000000");

        assertSame(NO_CONTENT, resp.getStatusCode());
    }

    // assignment -> section -> 3.3
    @RepeatedTest(20)
    @Sql("test_success_remove_appointment_when_patients_have_concurrent_requests.sql")
    void test_success_remove_appointment_when_we_have_concurrent_requests() {
        try {
            var url = SERVICE_PATH + "?ref={ref}";
            Callable<ResponseEntity<ApiResp<Object>>> req = () -> client.exchange(
                    url, DELETE, HttpEntity.EMPTY, parameterized(Object.class), "R0000000000000000");
            List<Future<ResponseEntity<ApiResp<Object>>>> responses = executor.invokeAll(
                    IntStream.range(0, 4)
                            .mapToObj(i -> req)
                            .toList());

            var statuses = responses.stream()
                    .map(TestUtils::getResult)
                    .map(ResponseEntity::getStatusCode)
                    .collect(groupingBy(Function.identity(), Collectors.counting()));

            assertEquals(2, statuses.size());
            assertEquals(1L, statuses.get(NO_CONTENT));
            assertEquals(3L, statuses.get(NOT_FOUND));
        } catch (Exception ignored) {
        }
    }

    // assignment -> section -> 5.1
    @ParameterizedTest
    @MethodSource("com.blubank.doctorappointment.utils.TestUtils#generateInvalidBooking")
    void test_failed_book_appointments_when_request_value_is_invalid(Booking booking) {
        var resp = client.exchange(SERVICE_PATH, PATCH, new HttpEntity<>(booking), parameterized(Reservation.class));
        assertSame(BAD_REQUEST, resp.getStatusCode());
        var errors = assertFailedRespAndExtractErrors(resp);
        assertEquals(1, errors.size());
    }

    // assignment -> section -> 5.2
    @Test
    @Sql("test_failed_book_appointment_when_it_has_been_taken.sql")
    void test_failed_book_appointment_when_it_has_been_taken() {
        var booking = Booking.builder().ref("R0000000000000000").name("Amin").phone("09163412114").build();
        var resp = client.exchange(SERVICE_PATH, PATCH, new HttpEntity<>(booking), parameterized(Reservation.class));

        assertSame(NOT_ACCEPTABLE, resp.getStatusCode());
        assertMissedAppointment(resp);
    }

    // assignment -> section -> 5.2
    @Test
    @Sql("test_failed_book_appointment_when_it_is_not_open.sql")
    void test_failed_book_appointment_when_it_is_not_open() {
        var booking = Booking.builder().ref("R0000000000000000").name("Amin").phone("09163412114").build();
        var resp = client.exchange(SERVICE_PATH, PATCH, new HttpEntity<>(booking), parameterized(Reservation.class));

        assertSame(NOT_FOUND, resp.getStatusCode());
        assertNotExistAppointment(resp);
    }

    // assignment -> section -> 5.3
    @RepeatedTest(20)
    @Sql("test_success_book_appointment_when_we_have_concurrent_requests.sql")
    void test_success_book_appointment_when_we_have_concurrent_requests() {
        try {
            IntFunction<Callable<ResponseEntity<ApiResp<Reservation>>>> req = i -> () -> client.exchange(
                    SERVICE_PATH, PATCH,
                    new HttpEntity<>(
                            Booking.builder().ref("R0000000000000000").name("Amin" + i).phone("0916341211" + i).build()
                    ),
                    parameterized(Reservation.class));

            List<Future<ResponseEntity<ApiResp<Reservation>>>> responses = executor.invokeAll(
                    IntStream.range(0, 4)
                            .mapToObj(req)
                            .toList());

            var statuses = responses.stream()
                    .map(TestUtils::getResult)
                    .map(ResponseEntity::getStatusCode)
                    .collect(groupingBy(Function.identity(), Collectors.counting()));

            assertEquals(2, statuses.size());
            assertEquals(1L, statuses.get(OK));
            assertEquals(3L, statuses.get(NOT_ACCEPTABLE));
        } catch (Exception ignored) {
        }
    }

    // assignment -> section -> 6.1
    @Test
    @Sql("test_success_get_appointments_by_phone_when_reserved_records_do_not_exist.sql")
    void test_success_get_appointments_by_phone_when_reserved_records_do_not_exist() {
        var type = ResolvableType.forClassWithGenerics(List.class, Reservation.class);
        var url = SERVICE_PATH + "/{phone}";
        var resp = client.exchange(
                url, GET, HttpEntity.EMPTY, TestUtils.<List<Reservation>>parameterized(type), "09163412114");

        assertSame(OK, resp.getStatusCode());
        var result = assertSuccessRespAndExtractResult(resp);
        assertTrue(result.isEmpty());
    }

    // assignment -> section -> 6.2
    @ParameterizedTest
    @ValueSource(strings = {"09163412114", "09163411421"})
    @Sql("test_success_get_appointments_by_phone_when_there_are_some_reserved_records.sql")
    void test_success_get_appointments_by_phone_when_there_are_some_reserved_records(String phone) {
        var type = ResolvableType.forClassWithGenerics(List.class, Reservation.class);
        var url = SERVICE_PATH + "/{phone}";
        var resp = client.exchange(
                url, GET, HttpEntity.EMPTY, TestUtils.<List<Reservation>>parameterized(type), phone);

        assertSame(OK, resp.getStatusCode());
        var result = assertSuccessRespAndExtractResult(resp);
        assertEquals(2, result.size());
        assertThat(result.get(0)).isNotNull()
                .hasNoNullFieldsOrProperties()
                .hasFieldOrPropertyWithValue("status", TAKEN)
                .hasFieldOrPropertyWithValue("phone", phone);
        assertThat(result.get(1)).isNotNull()
                .hasNoNullFieldsOrProperties()
                .hasFieldOrPropertyWithValue("status", TAKEN)
                .hasFieldOrPropertyWithValue("phone", phone);
    }

    @AfterAll
    static void afterAll() {
        executor.shutdownNow();
    }

    private void doPlaning(DailySchedule schedule) {
        var resp = client.exchange(SERVICE_PATH, POST, new HttpEntity<>(schedule), parameterized(Long.class));
        assertSame(BAD_REQUEST, resp.getStatusCode());
        var errors = assertFailedRespAndExtractErrors(resp);
        assertEquals(1, errors.size());
    }
}
