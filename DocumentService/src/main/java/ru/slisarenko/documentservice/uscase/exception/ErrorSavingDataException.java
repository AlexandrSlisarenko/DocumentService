package ru.slisarenko.documentservice.uscase.exception;

import ru.slisarenko.documentservice.enums.ErrorCode;

import static ru.slisarenko.documentservice.enums.ErrorCode.SavingData;

public class ErrorSavingDataException extends RuntimeException {
    public ErrorSavingDataException(String message) {
        super(SavingData.getCode() + ":" + SavingData.getMessage()+ "=>" + message);
    }
}
