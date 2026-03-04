package ru.slisarenko.documentservice.enums;

import lombok.Getter;


@Getter
public enum ErrorCode {

    EmptyAndLength(001, "Empty or invalid length in field"),
    SavingData(002, "Error when saving data to the database"),
    DocumentNotFound(003, "Document not found in db"),
    HistoryElementNotFound(004, "History element not found");

    private final Integer code;
    private final String message;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
