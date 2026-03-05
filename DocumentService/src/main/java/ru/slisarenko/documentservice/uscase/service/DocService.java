package ru.slisarenko.documentservice.uscase.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.slisarenko.documentservice.enums.Command;
import ru.slisarenko.documentservice.enums.Status;
import ru.slisarenko.documentservice.persist.model.DocumentEntity;
import ru.slisarenko.documentservice.persist.model.HistoryEntity;
import ru.slisarenko.documentservice.uscase.dto.DocumentFieldDTO;

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

    public HistoryEntity submittedDocument(DocumentEntity document, String userName, String comment) {
        document = this.documentPersistentService.updateDocument(document.getUuid(), Status.SUBMITTED);
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
}
