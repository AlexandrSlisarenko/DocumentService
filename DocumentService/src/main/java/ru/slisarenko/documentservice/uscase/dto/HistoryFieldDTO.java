package ru.slisarenko.documentservice.uscase.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import ru.slisarenko.documentservice.enums.Command;
import ru.slisarenko.documentservice.enums.Status;

public record HistoryFieldDTO(long id,
                              UUID uuid,
                              String authorChang,
                              String comment,
                              Command command,
                              Status status,
                              LocalDateTime changeTime) {
}
