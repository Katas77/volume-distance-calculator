package ru.fastdelivery.presentation.exception;

public class InvalidDimensionsException extends RuntimeException {
    public InvalidDimensionsException() {
    }

    public InvalidDimensionsException(Exception cause) {
        super(cause);
    }

    public InvalidDimensionsException(String message) {
        super(message);
    }

    public InvalidDimensionsException(String message, Exception cause) {
        super(message, cause);
    }

}