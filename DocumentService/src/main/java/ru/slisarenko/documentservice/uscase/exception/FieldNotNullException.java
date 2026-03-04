package ru.slisarenko.documentservice.uscase.exception;

public class FieldNotNullException extends RuntimeException {
    public FieldNotNullException(String message) {
        super(message);
    }
}
