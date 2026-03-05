package ru.slisarenko.documentservice.uscase.service;

import java.time.LocalDateTime;
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

import static ru.slisarenko.documentservice.uscase.utils.Constants.USER_CREATER;
import static ru.slisarenko.documentservice.uscase.utils.Constants.USER_TESTER;
import static ru.slisarenko.documentservice.uscase.utils.Constants.USER_VERIFUING;

@SpringBootTest
@Testcontainers
@Import(MyTestContainer.class)
class DocServiceTest {
    private static final UUID UUID_ID = UUID.fromString("12ad5948-3457-45bd-b23a-659ab42bf75f");

    @Autowired
    private DocService documentService;

    @Test
    void createDocument_happyPath_ReturnHistory() {

        var history  = getHistoryNewDocument();
        Assertions.assertNotNull(history);
        Assertions.assertNotNull(history.getId());
        Assertions.assertNotNull(history.getUuid());
        Assertions.assertNotNull(history.getChangeTime());
        Assertions.assertEquals(USER_TESTER, history.getAuthorChang());
        Assertions.assertEquals(Status.DRAFT, history.getStatus());
        Assertions.assertEquals(Command.Create, history.getCommand());
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
        document.setStatus(Status.SUBMITTED);
        document.setChangeTime(LocalDateTime.now());
        var newHistory = this.documentService.submittedDocument(document, USER_TESTER, "документ проверен");
        Assertions.assertNotNull(newHistory);
        Assertions.assertEquals(2L, newHistory.getId());
        Assertions.assertEquals(history.getUuid(), newHistory.getUuid());
    }

    @Test
    void approveDocument_happyPath_ReturnHistory() {
        var historyDRAFT  = getHistoryNewDocument();
        Assertions.assertEquals(Status.DRAFT, historyDRAFT.getStatus());
        var document = this.documentService.getDocumentByUUID(historyDRAFT.getUuid());
        document.setStatus(Status.SUBMITTED);
        document.setChangeTime(LocalDateTime.now());
        var historySUBMITTED = this.documentService.submittedDocument(document, USER_TESTER, "документ проверен");
        Assertions.assertEquals(Status.SUBMITTED, historySUBMITTED.getStatus());
        document = this.documentService.getDocumentByUUID(historySUBMITTED.getUuid());
        document.setStatus(Status.APPROVED);
        document.setChangeTime(LocalDateTime.now());
        var historyAPPROVED = this.documentService.approvedDocument(document.getUuid(), USER_VERIFUING, "документ занесен в реестр");
        Assertions.assertEquals(Status.APPROVED, historyAPPROVED.getStatus());
    }


}