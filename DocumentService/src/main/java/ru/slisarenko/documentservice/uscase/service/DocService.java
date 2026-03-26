package ru.slisarenko.documentservice.uscase.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.slisarenko.documentservice.enums.Command;
import ru.slisarenko.documentservice.enums.Status;
import ru.slisarenko.documentservice.persist.model.DocumentDataEntity;
import ru.slisarenko.documentservice.persist.model.DocumentEntity;
import ru.slisarenko.documentservice.persist.model.HistoryEntity;
import ru.slisarenko.documentservice.persist.service.DocumentDataService;
import ru.slisarenko.documentservice.persist.service.DocumentPersistentService;
import ru.slisarenko.documentservice.persist.service.HistoryPersistentService;
import ru.slisarenko.documentservice.uscase.dto.DocumentFieldDTO;
import ru.slisarenko.documentservice.uscase.dto.DocumentWithHistoryDTO;
import ru.slisarenko.documentservice.uscase.dto.FilterDTO;
import ru.slisarenko.documentservice.uscase.exception.DocumentIsApprovedException;
import ru.slisarenko.documentservice.uscase.exception.HistoryElementNotFoundException;

import static ru.slisarenko.documentservice.uscase.utils.Constants.USER_CREATOR;

@Service
@RequiredArgsConstructor
public class DocService {


    private final DocumentPersistentService documentPersistentService;
    private final HistoryPersistentService historyPersistentService;
    private final ApprovalRegisterService approvalRegisterService;
    private final DocumentDataService documentDataService;


    public HistoryEntity createDocument(DocumentFieldDTO documentFieldDTO, String comment) {
        var document = this.documentPersistentService.createNewDocument(documentFieldDTO);
        return this.historyPersistentService.saveHistoryDocument(document, USER_CREATOR, Command.Create, comment);
    }

    public DocumentEntity getDocumentByUUID(UUID uuid) {
        return this.documentPersistentService.getDocumentByUUID(uuid);
    }

    public HistoryEntity sendToSubmitted(UUID documentUuid, String userName, String comment) {
        var actualHistoryExists = this.historyPersistentService.existsActualHistory(documentUuid, Status.DRAFT);
        if(actualHistoryExists){
            var document = this.documentPersistentService.updateDocument(documentUuid, Status.SUBMITTED);
            return this.historyPersistentService.saveHistoryDocument(document, userName, Command.Update, comment);
        } else {
            throw new HistoryElementNotFoundException("Uuid = " + documentUuid + ", status = " + Status.DRAFT);
        }

    }
    @Transactional
    public HistoryEntity approvedDocument(UUID documentUUID, String userName, String comment) {
        var actualHistoryExists = this.historyPersistentService.existsActualHistory(documentUUID, Status.SUBMITTED);
        if(actualHistoryExists){
            var document = this.documentPersistentService.updateDocument(documentUUID, Status.APPROVED);
            var isApproved = this.approvalRegisterService.addDocument(documentUUID);
            if(isApproved){
                return this.historyPersistentService.saveHistoryDocument(document, userName, Command.Update, comment);
            } else {
                this.documentPersistentService.updateDocument(documentUUID, Status.SUBMITTED);
                throw new DocumentIsApprovedException("Uuid = " + documentUUID + ".");
            }
        } else {
            throw new HistoryElementNotFoundException("Uuid = " + documentUUID + ", status = " + Status.SUBMITTED);
        }

    }

    public DocumentWithHistoryDTO getDocumentWithHistory(UUID uuidDoc) {
        var document = this.documentPersistentService.getDocumentByUUID(uuidDoc);
        var history = this.historyPersistentService.getAllHistoryByUuid(uuidDoc);
        return DocumentWithHistoryDTO.builder()
                .document(document)
                .history(history)
                .build();
    }

    public Page<DocumentEntity> getDocuments(List<UUID> uuids, int page, int size, String sort, String ascDesc) {
        return this.documentPersistentService.getDocuments(uuids, page, size, sort, ascDesc);
    }

    public DocumentFieldDTO createDocumentFieldDTO(String name, String author) {
        return DocumentFieldDTO.builder()
                .author(author)
                .name(name)
                .build();
    }

    public DocumentDataEntity saveDocumentData(UUID id,  String text) {
        return this.documentDataService.save(id, text);
    }


    public int deleteDocuments(List<UUID> deleteList) {
        return this.documentPersistentService.deleteDocuments(deleteList);
    }

    public int deleteHistoryDocuments(List<UUID> deleteHistoryDocuments) {
        return this.historyPersistentService.deleteBatch(deleteHistoryDocuments);
    }

    public boolean existsByUUID(UUID uuid) {
        return this.documentPersistentService.existsDocument(uuid);
    }

    public Page<DocumentWithHistoryDTO> findDocumentsByFilter(FilterDTO filter) {

        var documents = this.documentPersistentService.findDocumentsUuidByFilter(filter);
        var history = this.historyPersistentService.findDocumentUuidByFilter(filter);
        var uuids = new HashSet<UUID>();
        uuids.addAll(documents);
        uuids.addAll(history);
        return getPageDocumentWithHistoryDTO(uuids, filter.pageNumber(), filter.countElement());
    }

    private Page<DocumentWithHistoryDTO> getPageDocumentWithHistoryDTO(HashSet<UUID> uuids, int page, int count) {
        var documents = uuids.stream().map(uuid -> {
            return DocumentWithHistoryDTO.builder()
                    .document(this.documentPersistentService.getDocumentByUUID(uuid))
                    .history(this.historyPersistentService.getAllHistoryByUuid(uuid))
                    .build();
        }).toList();
        var result = PageRequest.of(page, count);
        var start = (int) result.getOffset();
        int end = Math.min((start + result.getPageSize()), documents.size());
        var pageContent = documents.subList(start, end);
        return new PageImpl<>(pageContent, result, documents.size());
    }

    public Page<DocumentWithHistoryDTO> findDocumentsBySpecificationFilter(FilterDTO filter) {
        var result = PageRequest.of(filter.pageNumber(), filter.countElement());
        var content = new ArrayList<DocumentWithHistoryDTO>();

        var documents = this.documentPersistentService.searchDocumentsBySpecification(filter);
        var history = this.historyPersistentService.searchDocumentsHistoryBySpecification(filter);

        var documentUuidInHistory = getDocumentsUuid(documents, history);
        for (UUID uuid : documentUuidInHistory) {
            var historyElements = history.stream().filter(f -> f.getUuid().equals(uuid)).toList();
            var doc = documents.stream()
                    .filter(f -> f.getUuid().equals(uuid))
                    .findFirst()
                    .orElse(DocumentEntity.builder().build());
            content.add(DocumentWithHistoryDTO.builder()
                    .history(historyElements)
                    .document(doc)
                    .build());
            documents.remove(doc);
            history.removeIf(f -> f.getUuid().equals(uuid));
        }
        documents.forEach(element -> content.add(getDocumentWithHistory(element.getUuid())));
        history.forEach(element -> content.add(getDocumentWithHistory(element.getUuid())));
        return new PageImpl<>(content, result, documents.size());
    }

    private Set<UUID> getDocumentsUuid(List<DocumentEntity>listDocuments, List<HistoryEntity>listHistory) {
        var uuids = new HashSet<UUID>();
        listDocuments.forEach(d -> uuids.add(d.getUuid()));
        listHistory.forEach(h -> uuids.add(h.getUuid()));
        return uuids;
    }


}
