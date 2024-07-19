package com.blubank.doctorappointment.api;

import com.blubank.doctorappointment.commons.dto.view.Reservation;
import com.blubank.doctorappointment.commons.model.Appointment;
import com.blubank.doctorappointment.commons.model.Status;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {

    boolean existsByDayAndStatusIn(LocalDate day, EnumSet<Status> statuses);

    boolean existsByRefAndStatus(String ref, Status status);

    List<Reservation> findByDayAndStatusIn(LocalDate day, EnumSet<Status> statuses);

    Reservation findByRef(String ref);

    List<Reservation> findByPhone(String phone);

    @Transactional
    @Modifying
    @Query("""
            UPDATE Appointment a
                SET a.status = :#{#criteria.values['status']}
            WHERE a.ref = :#{#criteria.wheres['ref']}
              AND a.status = :#{#criteria.wheres['status']}
            """)
    int updateStatusByRefAndStatus(@Param("criteria") Criteria criteria);

    @Transactional
    @Modifying
    @Query("""
            UPDATE Appointment a
                SET a.status = :#{#criteria.values['status']},
                    a.name = :#{#criteria.values['name']},
                    a.phone = :#{#criteria.values['phone']}
            WHERE a.ref = :#{#criteria.wheres['ref']}
              AND a.status = :#{#criteria.wheres['status']}
            """)
    int updateStatusAndNameAndPhoneByRefAndStatus(@Param("criteria") Criteria criteria);
}
