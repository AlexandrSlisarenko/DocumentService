package ru.slisarenko.documentservice.uscase.dto;

import java.util.List;
import lombok.Builder;
import ru.slisarenko.documentservice.persist.model.DocumentEntity;
import ru.slisarenko.documentservice.persist.model.HistoryEntity;

@Builder
public record DocumentWithHistoryDTO(DocumentEntity document, List<HistoryEntity> history){
}
