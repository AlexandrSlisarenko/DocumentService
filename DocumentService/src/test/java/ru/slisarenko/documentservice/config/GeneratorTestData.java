package ru.slisarenko.documentservice.config;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import ru.slisarenko.documentservice.persist.model.DocumentEntity;
import ru.slisarenko.documentservice.persist.model.HistoryEntity;
import ru.slisarenko.documentservice.uscase.dto.DocumentFieldDTO;
import ru.slisarenko.documentservice.uscase.service.DocService;

import static ru.slisarenko.documentservice.uscase.utils.Constants.USER_CREATER;
import static ru.slisarenko.documentservice.uscase.utils.Constants.USER_TESTER;
import static ru.slisarenko.documentservice.uscase.utils.Constants.USER_VERIFUING;

@Component
public class GeneratorTestData {

    private final DocService documentService;

    public GeneratorTestData(DocService documentService) {
        this.documentService = documentService;
    }

    public @NotNull List<UUID> generateDocumentsTestData(int coundDoc, String status) {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        List<Future<List<UUID>>> futures = new ArrayList<>();

        for (int i = 0; i < coundDoc; i++) {
            futures.add(executorService.submit(() -> {
                List<UUID> partial = new ArrayList<>();
                partial.add(generateDocumentData(status));
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


    private UUID generateDocumentData(String status){
        var historyDRAFT  = getHistoryNewDocument();
        DocumentEntity document = null;
        switch (status){
            case "DRAFT":
                return historyDRAFT.getUuid();
            case "SUBMITTED":
                document = this.documentService.getDocumentByUUID(historyDRAFT.getUuid());
                return this.documentService.submittedDocument(document.getUuid(), USER_TESTER, "документ проверен").getUuid();
            case "APPROVED":
                document = this.documentService.getDocumentByUUID(historyDRAFT.getUuid());
                var historySUBMITTED = this.documentService.submittedDocument(document.getUuid(), USER_TESTER, "документ проверен");
                document = this.documentService.getDocumentByUUID(historySUBMITTED.getUuid());
                return this.documentService.approvedDocument(document.getUuid(), USER_VERIFUING, "документ занесен в реестр").getUuid();
                default:
                    return null;
        }
    }

    private HistoryEntity getHistoryNewDocument() {
        var documentFields = DocumentFieldDTO.builder()
                .author(USER_CREATER)
                .name("name123")
                .build();

        return this.documentService.createDocument(documentFields, "тестовое сохранение");
    }
}
