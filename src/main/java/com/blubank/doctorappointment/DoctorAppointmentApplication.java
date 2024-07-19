package com.blubank.doctorappointment;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableAdminServer
@SpringBootApplication
@EnableJpaRepositories
public class DoctorAppointmentApplication {

	public static void main(String[] args) {
		SpringApplication.run(DoctorAppointmentApplication.class, args);
	}
}
