package ru.slisarenko.documentservice.persist.mapper;

import org.springframework.stereotype.Component;
import ru.slisarenko.documentservice.persist.model.DocumentEntity;
import ru.slisarenko.documentservice.persist.model.HistoryEntity;
import ru.slisarenko.documentservice.uscase.dto.FilterDTO;

@Component
public class FilterMapper {

    public DocumentEntity toDocumentEntity(FilterDTO filterDTO) {
        return DocumentEntity.builder()
                .name(filterDTO.nameDocument())
                .author(filterDTO.author())
                .changeTime(filterDTO.createTime())
                .status(filterDTO.statusDocument())
                .build();
    }

    public HistoryEntity toHistoryEntity(FilterDTO filterDTO) {
        return HistoryEntity.builder()
                .authorChang(filterDTO.updateAuthor())
                .comment(filterDTO.inComment())
                .command(filterDTO.command())
                .changeTime(filterDTO.updateTime())
                .build();
    }
}
