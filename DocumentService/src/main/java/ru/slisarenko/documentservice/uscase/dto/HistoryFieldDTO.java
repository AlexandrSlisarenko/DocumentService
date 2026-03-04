package ru.slisarenko.documentservice.uscase.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record HistoryFieldDTO(Long id,
                              UUID uuid,
                              String authorChang,
                              String comment,
                              String command,
                              String status,
                              LocalDateTime changeTime) {
}
