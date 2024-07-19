package com.blubank.doctorappointment.commons.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static com.blubank.doctorappointment.commons.model.Status.OPEN;
import static javax.persistence.GenerationType.IDENTITY;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "appointment",
        indexes = {
                @Index(name = "day_status_search_clauses", columnList = "day, status"),
                @Index(name = "ref_status_search_clauses", columnList = "ref, status"),
                @Index(name = "ref_unique_constraint", columnList = "ref"),
                @Index(name = "phone_search_clauses", columnList = "phone")
        }
)
public class Appointment {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private int id;

    @Column(nullable = false)
    private LocalDate day;

    @Column(nullable = false, unique = true, length = 17, columnDefinition = "CHAR(17) DEFAULT 'R0000000000000000'")
    private String ref;

    @Column(nullable = false)
    private LocalTime start;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 30")
    private int duration; // in minuets

    @Convert(converter = StatusConverter.class)
    @Column(nullable = false, columnDefinition = "CHAR(1) DEFAULT 'O'")
    private Status status = OPEN;

    @Column(length = 100)
    private String name;

    @Column(length = 20)
    private String phone;

    @CreationTimestamp
    @Column(updatable = false, nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime created;
}
