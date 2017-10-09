package com.company.util;

public class SyntacticallyIncorrectRequestException extends RuntimeException {

    public SyntacticallyIncorrectRequestException(String message) {
        super(message);
    }

    public SyntacticallyIncorrectRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
