package ru.slisarenko.documentservice.uscase.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record DocumentFieldDTO(long id,
                               UUID uuid,
                               String name,
                               String author,
                               String status,
                               LocalDateTime changeTime) {
}
