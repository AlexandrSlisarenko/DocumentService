package ru.slisarenko.documentservice.uscase.exception;

import static ru.slisarenko.documentservice.enums.ErrorCode.DocumentNotReadable;

public class DocumentNotReadableException extends RuntimeException {
    public DocumentNotReadableException(String message) {
        super(DocumentNotReadable.getCode() + ": " + DocumentNotReadable.getMessage() + " => " + message);
    }
}
