package ru.slisarenko.documentservice.uscase.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.slisarenko.documentservice.persist.model.DocumentEntity;
import ru.slisarenko.documentservice.persist.repository.DocumentRepository;
import ru.slisarenko.documentservice.uscase.dto.DocumentFieldDTO;

@Service
@RequiredArgsConstructor
public class DocumentService {
    private final DocumentRepository documentRepository;

    public DocumentEntity createNewDocument(DocumentFieldDTO documentFields){
        var document = createDocumentEntity(documentFields);
        return this.documentRepository.save(document);
    }

    private DocumentEntity createDocumentEntity(DocumentFieldDTO fields) {
        return DocumentEntity.builder()
                .name(fields.name())
                .author(fields.author())
                .build();
    }
}
