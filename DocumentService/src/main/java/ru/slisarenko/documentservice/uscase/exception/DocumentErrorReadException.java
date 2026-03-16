package ru.slisarenko.documentservice.uscase.exception;

import static ru.slisarenko.documentservice.enums.ErrorCode.DocumentErrorRead;

public class DocumentErrorReadException extends RuntimeException {
    public DocumentErrorReadException(String message) {
      super(DocumentErrorRead.getCode() + ": " + DocumentErrorRead.getMessage() + "=>" + message);
    }
}
