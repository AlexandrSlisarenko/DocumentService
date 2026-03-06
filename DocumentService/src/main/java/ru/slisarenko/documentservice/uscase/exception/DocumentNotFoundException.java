package ru.slisarenko.documentservice.uscase.exception;

import static ru.slisarenko.documentservice.enums.ErrorCode.DocumentNotFound;

public class DocumentNotFoundException extends RuntimeException {
    public DocumentNotFoundException(String message) {
        super(DocumentNotFound.getCode() + ": " + DocumentNotFound.getMessage() + " => " + message);
    }
}
