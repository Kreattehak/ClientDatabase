package com.company.util;

public class ProcessUserRequestException extends RuntimeException {

    public ProcessUserRequestException(String message) {
        super(message);
    }

    public ProcessUserRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
