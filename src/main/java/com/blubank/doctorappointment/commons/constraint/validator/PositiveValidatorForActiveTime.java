package com.blubank.doctorappointment.commons.constraint.validator;

import com.blubank.doctorappointment.commons.dto.ActivePeriod;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Positive;
import java.time.Duration;

public class PositiveValidatorForActiveTime implements ConstraintValidator<Positive, ActivePeriod> {

    @Override
    public boolean isValid(ActivePeriod value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return !Duration.between(value.getStart(), value.getEnd()).isNegative();
    }
}
