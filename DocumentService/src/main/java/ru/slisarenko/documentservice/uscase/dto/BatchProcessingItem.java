package ru.slisarenko.documentservice.uscase.dto;

import java.util.UUID;
import lombok.Builder;
import ru.slisarenko.documentservice.enums.StatusBatchProcessing;

@Builder
public record BatchProcessingItem(UUID id, StatusBatchProcessing statusBatchProcessing) {
}
