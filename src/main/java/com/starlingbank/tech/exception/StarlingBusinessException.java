package com.starlingbank.tech.exception;

public class StarlingBusinessException extends RuntimeException {
    private static final long serialVersionUID = -5704354545244956L;

    public StarlingBusinessException() {
        super();
    }

    public StarlingBusinessException(String errorMessage) {
        super(errorMessage);
    }

    public StarlingBusinessException(Throwable cause) {
        super(cause);
    }

    public StarlingBusinessException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }

    public StarlingBusinessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
