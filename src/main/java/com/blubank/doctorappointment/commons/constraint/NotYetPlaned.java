package com.blubank.doctorappointment.commons.constraint;

import com.blubank.doctorappointment.commons.constraint.validator.NotYetPlanedValidatorForLocalDate;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static com.blubank.doctorappointment.commons.constraint.NotYetPlaned.List;

@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Repeatable(List.class)
@Documented
@Constraint(validatedBy = {NotYetPlanedValidatorForLocalDate.class})
public @interface NotYetPlaned {

    String message() default "{com.blubank.doctorappointment.commons.constraint.NotYetPlaned.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
    @Retention(RUNTIME)
    @Documented
    @interface List {

        NotYetPlaned[] value();
    }
}
