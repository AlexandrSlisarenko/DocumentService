package ru.slisarenko.documentservice.uscase.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import ru.slisarenko.documentservice.enums.Status;

@Builder
public record DocumentFieldDTO(long id,
                               UUID uuid,
                               String name,
                               String author,
                               Status status,
                               LocalDateTime changeTime) {
}
