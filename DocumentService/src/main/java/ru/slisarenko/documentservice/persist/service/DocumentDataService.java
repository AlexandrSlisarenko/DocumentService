package ru.slisarenko.documentservice.persist.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.slisarenko.documentservice.persist.model.DocumentDataEntity;
import ru.slisarenko.documentservice.persist.repository.DocumentDataRepository;

@Service
@RequiredArgsConstructor
public class DocumentDataService {
    private final DocumentDataRepository documentDataRepository;

    public DocumentDataEntity save(UUID id,  String text) {
        var data = DocumentDataEntity.builder()
                .text(text)
                .id(id)
                .build();
        return documentDataRepository.save(data);
    }

    public DocumentDataEntity findById(UUID id) {
        var dataEmpty = DocumentDataEntity.builder()
                .text("Документ не найден")
                .id(id)
                .build();
        return documentDataRepository.findById(id).orElse(dataEmpty);
    }
}
