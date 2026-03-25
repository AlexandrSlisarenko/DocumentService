package ru.slisarenko.documentservice.uscase.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.slisarenko.documentservice.config.MyTestContainer;
import ru.slisarenko.documentservice.enums.Command;
import ru.slisarenko.documentservice.enums.Status;
import ru.slisarenko.documentservice.persist.model.HistoryEntity;
import ru.slisarenko.documentservice.uscase.dto.DocumentFieldDTO;
import ru.slisarenko.documentservice.uscase.dto.DocumentWithHistoryDTO;
import ru.slisarenko.documentservice.uscase.dto.FilterDTO;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static ru.slisarenko.documentservice.uscase.utils.Constants.USER_APPROVER;
import static ru.slisarenko.documentservice.uscase.utils.Constants.USER_TESTER;
import static ru.slisarenko.documentservice.uscase.utils.Constants.USER_VERIFYING;

@SpringBootTest
@Testcontainers
@Import(MyTestContainer.class)
class DocServiceTest {
    @Autowired
    private DocService documentService;
    private List<UUID> uuids = new ArrayList<>();

    @Test
    void createDocument_happyPath_ReturnHistory() {

        var history  = getHistoryNewDocument();
        Assertions.assertNotNull(history);
        Assertions.assertNotNull(history.getId());
        Assertions.assertNotNull(history.getUuid());
        Assertions.assertNotNull(history.getChangeTime());
        assertEquals(USER_TESTER, history.getAuthorChang());
        assertEquals(Status.DRAFT, history.getStatus());
        assertEquals(Command.Create, history.getCommand());
    }

    private HistoryEntity getHistoryNewDocument() {
        var documentFields = DocumentFieldDTO.builder()
                .author(USER_TESTER)
                .name("name123")
                .build();

        return this.documentService.createDocument(documentFields, "тестовое сохранение");
    }

    @Test
    void updateDocument_happyPath_ReturnHistory() {
        var history  = getHistoryNewDocument();
        var document = this.documentService.getDocumentByUUID(history.getUuid());
        var newHistory = this.documentService.sendToSubmitted(document.getUuid(), USER_APPROVER, "документ проверен");
        Assertions.assertNotNull(newHistory);
        assertEquals(history.getUuid(), newHistory.getUuid());
    }

    @Test
    void approveDocument_happyPath_ReturnHistory() {
        var historyDRAFT  = getHistoryNewDocument();
        assertEquals(Status.DRAFT, historyDRAFT.getStatus());
        var document = this.documentService.getDocumentByUUID(historyDRAFT.getUuid());
        var historySUBMITTED = this.documentService.sendToSubmitted(document.getUuid(), USER_APPROVER, "документ проверен");
        assertEquals(Status.SUBMITTED, historySUBMITTED.getStatus());
        document = this.documentService.getDocumentByUUID(historySUBMITTED.getUuid());
        var historyAPPROVED = this.documentService.approvedDocument(document.getUuid(), USER_VERIFYING, "документ занесен в реестр");
        assertEquals(Status.APPROVED, historyAPPROVED.getStatus());
    }

    @Test
    void getDocumentWithHistory_ByUUID_DocumentWithHistory(){
        var uuidDoc = generateDocumentData();
        var documentWithHistory = this.documentService.getDocumentWithHistory(uuidDoc);
        assertEquals(uuidDoc, documentWithHistory.document().getUuid());
        assertEquals(3, documentWithHistory.history().size());
    }

    @Test
    void getFiveDocumentWithHistory_byListUUID_ReturnListDocumentWithHistory(){
        List<UUID> resultList = generateDocumentsTestData(50);
        int pages = 5, pageSize = 5;
        String sort = "changeTime";
        String ascDesc = "DESC";
        System.out.println(resultList.size());
        var result = this.documentService.getDocuments(resultList, pages, pageSize, sort, ascDesc);
        assertNotNull(result);
    }

    public @NotNull List<UUID> generateDocumentsTestData(int coundDoc) {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        List<Future<List<UUID>>> futures = new ArrayList<>();

        for (int i = 0; i < coundDoc; i++) {
            futures.add(executorService.submit(() -> {
                List<UUID> partial = new ArrayList<>();
                partial.add(generateDocumentData());
                return partial;
            }));
        }
        executorService.close();
        List<UUID> resultList = new ArrayList<>();
        for (Future<List<UUID>> future : futures) {
            try {
                resultList.addAll(future.get()); // get() блокируется до завершения задачи
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        return resultList;
    }


    private UUID generateDocumentData(){
        var historyDRAFT  = getHistoryNewDocument();
        var document = this.documentService.getDocumentByUUID(historyDRAFT.getUuid());
        var historySUBMITTED = this.documentService.sendToSubmitted(document.getUuid(), USER_TESTER, "документ проверен");
        document = this.documentService.getDocumentByUUID(historySUBMITTED.getUuid());
        var historyAPPROVED = this.documentService.approvedDocument(document.getUuid(), USER_VERIFYING, "документ занесен в реестр");
        return historyAPPROVED.getUuid();
    }


    @Test
    void getDocumentsWithHistoryByUser_Alex_ReturnDocumentsWithHistory() {
        var countDocument = 10;
        List<UUID> resultList = generateDocumentsTestData(countDocument);
        resultList.forEach(uuid -> {
            var document = this.documentService.getDocumentByUUID(uuid);
            this.documentService.sendToSubmitted(document.getUuid(), USER_APPROVER, "документ проверен");
        });
        var filter = FilterDTO.builder()
                .author(USER_TESTER)
                .countElement(5)
                .pageNumber(0)
                .build();
        Page<DocumentWithHistoryDTO> documents = this.documentService.findDocumentsByFilter(filter);
        Assertions.assertNotNull(documents);
        Assertions.assertEquals(countDocument, documents.getTotalElements());
    }

    @Test
    void getDocumentsWithHistoryByUser_ApproverUser_CreatedDocuments_ReturnEmptyList() {
        var countDocument = 10;
        var random = new Random();
        for (int i = 0; i < countDocument; i++) {
            var documentFields = DocumentFieldDTO.builder()
                    .author(USER_TESTER)
                    .name("name" + random.nextInt())
                    .build();

            this.documentService.createDocument(documentFields, "тестовое сохранение");
        }
        var filter = FilterDTO.builder()
                .updateAuthor(USER_APPROVER)
                .build();
        Page<DocumentWithHistoryDTO> documents = this.documentService.findDocumentsByFilter(filter);
        Assertions.assertNotNull(documents);
        Assertions.assertEquals(0, documents.getTotalElements());
    }

    @Test
    void getUpdateDocumentsByUser_TesterAndApprover_ReturnDocumentsWithHistory() {
        var countCreateDocument = 10;
        var countApproveDocument = 2;
        List<UUID> resultList = generateDocumentsTestData(countCreateDocument);
        for(int i = 0; i< countApproveDocument; i++){
            var document = this.documentService.getDocumentByUUID(resultList.get(i));
            this.documentService.sendToSubmitted(document.getUuid(), USER_APPROVER, "документ проверен");
        }
        var filter = FilterDTO.builder()
                .author(USER_TESTER)
                .updateAuthor(USER_APPROVER)
                .countElement(5)
                .pageNumber(0)
                .build();
        Page<DocumentWithHistoryDTO> documents = this.documentService.findDocumentsByFilter(filter);
        Assertions.assertNotNull(documents);
        Assertions.assertEquals(countApproveDocument, documents.getTotalElements());
    }
}