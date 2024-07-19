package com.blubank.doctorappointment.commons.constraint.validator;

import com.blubank.doctorappointment.api.AppointmentManager;
import com.blubank.doctorappointment.commons.constraint.NotYetPlaned;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

@Component
public class NotYetPlanedValidatorForLocalDate implements ConstraintValidator<NotYetPlaned, LocalDate> {

    private final AppointmentManager manager;

    public NotYetPlanedValidatorForLocalDate(AppointmentManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return !manager.isPlaned(value);
    }
}
