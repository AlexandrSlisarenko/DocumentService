package ru.slisarenko.documentservice.uscase.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ru.slisarenko.documentservice.enums.Command;
import ru.slisarenko.documentservice.enums.Status;
import ru.slisarenko.documentservice.persist.model.DocumentEntity;
import ru.slisarenko.documentservice.persist.model.HistoryEntity;
import ru.slisarenko.documentservice.uscase.dto.DocumentFieldDTO;
import ru.slisarenko.documentservice.uscase.dto.DocumentWithHistoryDTO;

import static ru.slisarenko.documentservice.uscase.utils.Constants.USER_CREATER;

@Service
@RequiredArgsConstructor
public class DocService {


    private final DocumentPersistentService documentPersistentService;
    private final HistoryPersistentService historyPersistentService;
    private final ApprovalRegisterService approvalRegisterService;


    public HistoryEntity createDocument(DocumentFieldDTO documentFieldDTO, String comment) {
        var document = this.documentPersistentService.createNewDocument(documentFieldDTO);
        return this.historyPersistentService.saveHistoryDocument(document, USER_CREATER, Command.Create, comment);
    }


    public DocumentEntity getDocumentByUUID(UUID uuid) {
        return this.documentPersistentService.getDocumentByUUID(uuid);
    }

    public HistoryEntity submittedDocument(UUID documentUuid, String userName, String comment) {
        var document = this.documentPersistentService.updateDocument(documentUuid, Status.SUBMITTED);
        return this.historyPersistentService.saveHistoryDocument(document, userName, Command.Update, comment);
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
}
