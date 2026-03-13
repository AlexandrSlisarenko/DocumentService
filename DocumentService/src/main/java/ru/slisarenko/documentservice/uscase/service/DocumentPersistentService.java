package ru.slisarenko.documentservice.uscase.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.slisarenko.documentservice.enums.Status;
import ru.slisarenko.documentservice.persist.model.DocumentEntity;
import ru.slisarenko.documentservice.persist.repository.DocumentRepository;
import ru.slisarenko.documentservice.uscase.dto.DocumentFieldDTO;
import ru.slisarenko.documentservice.uscase.exception.DocumentNotFoundException;
import ru.slisarenko.documentservice.uscase.exception.EmptyAndLengthException;
import ru.slisarenko.documentservice.uscase.utils.CheckField;

@Service
@RequiredArgsConstructor
public class DocumentPersistentService {
    private final DocumentRepository documentRepository;

    public DocumentEntity createNewDocument(DocumentFieldDTO documentFields) {
        checkDocumentFiles(documentFields);
        var document = createDocumentEntity(documentFields);
        var documentId = documentRepository.save(document).getId();
        return this.documentRepository.findById(documentId).orElseThrow();
    }

    public DocumentEntity getDocumentByUUID(UUID uuid) {
        try {
            return this.documentRepository.findByUuid(uuid);
        }catch (Exception e) {
            throw new DocumentNotFoundException(" Id = " + uuid.toString() + " "+ e.getMessage());
        }
    }

    public DocumentEntity updateDocument(UUID documentUuid, Status documentStatus) {
        var documentFromDB = getDocumentByUUID(documentUuid);
        documentFromDB.setStatus(documentStatus);
        documentFromDB.setChangeTime(LocalDateTime.now());
        var documentId = documentRepository.save(documentFromDB).getId();
        return this.documentRepository.findById(documentId).orElseThrow();
    }

    public List<UUID> getDocumentsIdByStatus(Status status, int size) {
        var limit  = Limit.of(size);
        return this.documentRepository.findUuidListByStatus(status, limit);
    }

    public Page<DocumentEntity> getDocuments(List<UUID> uuids, int page, int size, String sort, String ascDesc) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.valueOf(ascDesc), sort));
        return this.documentRepository.findDocumentEntitiesByIds(uuids,pageable);
    }

    private DocumentEntity createDocumentEntity(DocumentFieldDTO fields) {
        return DocumentEntity.builder()
                .name(fields.name())
                .author(fields.author())
                .build();
    }

    private void checkDocumentFiles(DocumentFieldDTO fields) {
        if (CheckField.checkEmptyAndLength(fields.name())) {
            throw new EmptyAndLengthException("Name");
        }
        if (CheckField.checkEmptyAndLength(fields.author())) {
            throw new EmptyAndLengthException("Author");
        }
    }


}
