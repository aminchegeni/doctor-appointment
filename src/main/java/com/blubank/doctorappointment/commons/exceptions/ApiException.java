package com.blubank.doctorappointment.commons.exceptions;

import com.blubank.doctorappointment.commons.dto.Error;
import lombok.Getter;

import java.io.Serial;

@Getter
public class ApiException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1417012952767208362L;

    protected final Error error;

    public ApiException(Error error) {
        this.error = error;
    }

    public ApiException(String message, Error error) {
        super(message);
        this.error = error;
    }

    public ApiException(String message, Throwable cause, Error error) {
        super(message, cause);
        this.error = error;
    }

    public ApiException(Throwable cause, Error error) {
        super(cause);
        this.error = error;
    }

    public ApiException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Error error) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.error = error;
    }
}
