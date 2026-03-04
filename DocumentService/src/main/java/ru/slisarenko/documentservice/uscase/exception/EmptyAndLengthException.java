package ru.slisarenko.documentservice.uscase.exception;

import static ru.slisarenko.documentservice.enums.ErrorCode.EmptyAndLength;

public class EmptyAndLengthException extends RuntimeException {
    public EmptyAndLengthException(String nameField) {
        super(EmptyAndLength.getCode() + ": " + EmptyAndLength.getMessage() + " " + nameField);
    }
}
