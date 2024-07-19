package com.blubank.doctorappointment.commons.exceptions;

import com.blubank.doctorappointment.commons.dto.Error;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@ResponseStatus(NOT_FOUND)
public class DoseNotExistException extends ApiException {

    @Serial
    private static final long serialVersionUID = -137828400791152490L;

    public DoseNotExistException(Error error) {
        super(error);
    }

    public DoseNotExistException(String message, Error error) {
        super(message, error);
    }

    public DoseNotExistException(String message, Throwable cause, Error error) {
        super(message, cause, error);
    }

    public DoseNotExistException(Throwable cause, Error error) {
        super(cause, error);
    }

    public DoseNotExistException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Error error) {
        super(message, cause, enableSuppression, writableStackTrace, error);
    }
}
