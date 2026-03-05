package ru.slisarenko.documentservice.uscase.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.slisarenko.documentservice.config.MyTestContainer;
import ru.slisarenko.documentservice.enums.Command;
import ru.slisarenko.documentservice.enums.Status;
import ru.slisarenko.documentservice.persist.model.HistoryEntity;
import ru.slisarenko.documentservice.uscase.dto.DocumentFieldDTO;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.slisarenko.documentservice.uscase.utils.Constants.USER_CREATER;
import static ru.slisarenko.documentservice.uscase.utils.Constants.USER_TESTER;
import static ru.slisarenko.documentservice.uscase.utils.Constants.USER_VERIFUING;

@SpringBootTest
@Testcontainers
@Import(MyTestContainer.class)
class DocServiceTest {
    @Autowired
    private DocService documentService;

    @Test
    void createDocument_happyPath_ReturnHistory() {

        var history  = getHistoryNewDocument();
        Assertions.assertNotNull(history);
        Assertions.assertNotNull(history.getId());
        Assertions.assertNotNull(history.getUuid());
        Assertions.assertNotNull(history.getChangeTime());
        assertEquals(USER_CREATER, history.getAuthorChang());
        assertEquals(Status.DRAFT, history.getStatus());
        assertEquals(Command.Create, history.getCommand());
    }

    private HistoryEntity getHistoryNewDocument() {
        var documentFields = DocumentFieldDTO.builder()
                .author(USER_CREATER)
                .name("name123")
                .build();

        return this.documentService.createDocument(documentFields, "тестовое сохранение");
    }

    @Test
    void updateDocument_happyPath_ReturnHistory() {
        var history  = getHistoryNewDocument();
        var document = this.documentService.getDocumentByUUID(history.getUuid());
        var newHistory = this.documentService.submittedDocument(document.getUuid(), USER_TESTER, "документ проверен");
        Assertions.assertNotNull(newHistory);
        assertEquals(history.getUuid(), newHistory.getUuid());
    }

    @Test
    void approveDocument_happyPath_ReturnHistory() {
        var historyDRAFT  = getHistoryNewDocument();
        assertEquals(Status.DRAFT, historyDRAFT.getStatus());
        var document = this.documentService.getDocumentByUUID(historyDRAFT.getUuid());
        var historySUBMITTED = this.documentService.submittedDocument(document.getUuid(), USER_TESTER, "документ проверен");
        assertEquals(Status.SUBMITTED, historySUBMITTED.getStatus());
        document = this.documentService.getDocumentByUUID(historySUBMITTED.getUuid());
        var historyAPPROVED = this.documentService.approvedDocument(document.getUuid(), USER_VERIFUING, "документ занесен в реестр");
        assertEquals(Status.APPROVED, historyAPPROVED.getStatus());
    }

    @Test
    void getDocumentWithHistory_ByUUID_DocumentWithHistory(){
        var uuidDoc = createData();
        var documentWithHistory = this.documentService.getDocumentWithHistory(uuidDoc);
        assertEquals(uuidDoc, documentWithHistory.document().getUuid());
        assertEquals(3, documentWithHistory.history().size());
    }

    @Test
    void getFiveDocumentWithHistory_byListUUID_ReturnListDocumentWithHistory(){
        List<UUID> uuids = new ArrayList<>();
        while (uuids.size() < 5) {
            uuids.add(createData());
        }


    }


    private UUID createData(){
        var historyDRAFT  = getHistoryNewDocument();
        var document = this.documentService.getDocumentByUUID(historyDRAFT.getUuid());
        var historySUBMITTED = this.documentService.submittedDocument(document.getUuid(), USER_TESTER, "документ проверен");
        document = this.documentService.getDocumentByUUID(historySUBMITTED.getUuid());
        var historyAPPROVED = this.documentService.approvedDocument(document.getUuid(), USER_VERIFUING, "документ занесен в реестр");
        return historyAPPROVED.getUuid();
    }

}