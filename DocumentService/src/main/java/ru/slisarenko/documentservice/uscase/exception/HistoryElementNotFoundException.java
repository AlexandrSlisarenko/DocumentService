package ru.slisarenko.documentservice.uscase.exception;

import static ru.slisarenko.documentservice.enums.ErrorCode.HistoryElementNotFound;

public class HistoryElementNotFoundException extends RuntimeException {
    public HistoryElementNotFoundException(String message)
    {
        super(HistoryElementNotFound.getCode() + ":" + HistoryElementNotFound.getMessage() + " => " + message);
    }
}
