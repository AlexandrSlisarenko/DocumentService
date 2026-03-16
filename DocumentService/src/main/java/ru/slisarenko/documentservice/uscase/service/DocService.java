package ru.slisarenko.documentservice.uscase.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ru.slisarenko.documentservice.enums.Command;
import ru.slisarenko.documentservice.enums.Status;
import ru.slisarenko.documentservice.persist.model.DocumentDataEntity;
import ru.slisarenko.documentservice.persist.model.DocumentEntity;
import ru.slisarenko.documentservice.persist.model.HistoryEntity;
import ru.slisarenko.documentservice.uscase.dto.DocumentFieldDTO;
import ru.slisarenko.documentservice.uscase.dto.DocumentWithHistoryDTO;
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

    public HistoryEntity approvedDocument(UUID documentUUID, String userName, String comment) {
        var isApproved = this.approvalRegisterService.addDocument(documentUUID);
        if(isApproved){
            var document = this.documentPersistentService.updateDocument(documentUUID, Status.APPROVED);
            return this.historyPersistentService.saveHistoryDocument(document, userName, Command.Update, comment);
        } else {
            return HistoryEntity.builder().build();
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
}
