package com.blubank.doctorappointment.commons.exceptions;

import com.blubank.doctorappointment.commons.dto.Error;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;

@ResponseStatus(NOT_ACCEPTABLE)
public class HasBeenTakenException extends ApiException {

    @Serial
    private static final long serialVersionUID = 127135292994743478L;

    public HasBeenTakenException(Error error) {
        super(error);
    }

    public HasBeenTakenException(String message, Error error) {
        super(message, error);
    }

    public HasBeenTakenException(String message, Throwable cause, Error error) {
        super(message, cause, error);
    }

    public HasBeenTakenException(Throwable cause, Error error) {
        super(cause, error);
    }

    public HasBeenTakenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Error error) {
        super(message, cause, enableSuppression, writableStackTrace, error);
    }
}
