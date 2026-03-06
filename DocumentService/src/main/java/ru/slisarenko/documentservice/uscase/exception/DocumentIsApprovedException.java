package ru.slisarenko.documentservice.uscase.exception;

import static ru.slisarenko.documentservice.enums.ErrorCode.DocumentNotFound;

public class DocumentIsApprovedException extends RuntimeException {
  public DocumentIsApprovedException(String message) {
    super(DocumentNotFound.getCode() + ": " + DocumentNotFound.getMessage() + " => " + message);
  }
}
