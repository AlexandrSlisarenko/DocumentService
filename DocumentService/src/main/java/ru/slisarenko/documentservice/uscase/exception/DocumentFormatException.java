package ru.slisarenko.documentservice.uscase.exception;

import static ru.slisarenko.documentservice.enums.ErrorCode.DocumentFormat;

public class DocumentFormatException extends RuntimeException {
    public DocumentFormatException(String message) {
        super(DocumentFormat.getCode() + ": " + DocumentFormat.getMessage() + "=>" + message);
    }
}
