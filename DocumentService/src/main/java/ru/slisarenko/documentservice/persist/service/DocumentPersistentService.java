package ru.slisarenko.documentservice.persist.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;
import org.springframework.stereotype.Service;
import ru.slisarenko.documentservice.enums.Status;
import ru.slisarenko.documentservice.persist.filter.DocumentSpecification;
import ru.slisarenko.documentservice.persist.mapper.FilterMapper;
import ru.slisarenko.documentservice.persist.model.DocumentEntity;
import ru.slisarenko.documentservice.persist.repository.DocumentRepository;
import ru.slisarenko.documentservice.uscase.dto.DocumentFieldDTO;
import ru.slisarenko.documentservice.uscase.dto.FilterDTO;
import ru.slisarenko.documentservice.uscase.exception.DocumentNotFoundException;
import ru.slisarenko.documentservice.uscase.exception.EmptyAndLengthException;
import ru.slisarenko.documentservice.uscase.utils.CheckField;

import static ru.slisarenko.documentservice.uscase.utils.CheckField.checkFilterDocumentEmpty;

@Service
@RequiredArgsConstructor
public class DocumentPersistentService {
    private final DocumentRepository documentRepository;
    private final FilterMapper filterMapper;

    public DocumentEntity createNewDocument(DocumentFieldDTO documentFields) {
        checkDocumentFiles(documentFields);
        var document = createDocumentEntity(documentFields);
        var documentId = documentRepository.save(document).getId();
        return this.documentRepository.findById(documentId).orElseThrow();
    }

    public DocumentEntity getDocumentByUUID(UUID uuid) {
        try {
            return this.documentRepository.findByUuid(uuid);
        } catch (Exception e) {
            throw new DocumentNotFoundException(" Id = " + uuid.toString() + " " + e.getMessage());
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
        var limit = Limit.of(size);
        return this.documentRepository.findUuidListByStatus(status, limit);
    }

    public Page<DocumentEntity> getDocuments(List<UUID> uuids, int page, int size, String sort, String ascDesc) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.valueOf(ascDesc), sort));
        return this.documentRepository.findDocumentEntitiesByIds(uuids, pageable);
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


    public int deleteDocuments(List<UUID> uuids) {
        try {
            return this.documentRepository.deleteAllByUuidBatch(uuids);
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean existsDocument(UUID uuid) {
        return this.documentRepository.existsByUuid(uuid);
    }

    public List<UUID> findDocumentsUuidByFilter(FilterDTO filter) {
        var paramFilterDocument = this.filterMapper.toDocumentEntity(filter);
        var macher = ExampleMatcher.matching()
                .withIgnoreNullValues()
                .withIgnorePaths("id")
                .withMatcher("name", matcher -> matcher.contains().ignoreCase())
                .withMatcher("author", matcher -> matcher.exact().ignoreCase())
                .withMatcher("status", matcher -> matcher.exact().ignoreCase());
        Example<DocumentEntity> example = Example.of(paramFilterDocument, macher);
        return this.documentRepository.findBy(example, FetchableFluentQuery::all)
                .stream().map(DocumentEntity::getUuid).toList();
    }

    public List<DocumentEntity> searchDocumentsBySpecification(FilterDTO criteria) {
        if (checkFilterDocumentEmpty(criteria)) {
            return new ArrayList<>();
        }

        Specification<DocumentEntity> spec = Specification.where(
                DocumentSpecification.filterDocumentsByAllParam(criteria));

        return documentRepository.findAll(spec);
    }
}
