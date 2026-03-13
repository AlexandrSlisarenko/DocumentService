package ru.slisarenko.documentservice.uscase.service;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.job.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.slisarenko.documentservice.config.BatchConfig;
import ru.slisarenko.documentservice.config.GeneratorTestData;
import ru.slisarenko.documentservice.config.MyTestContainer;
import ru.slisarenko.documentservice.enums.Status;
import ru.slisarenko.documentservice.uscase.dto.BatchProcessingItem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@Testcontainers
@Import({MyTestContainer.class, BatchConfig.class})
class BatchDocumentProcessingServiceTest {

    @Autowired
    private BatchDocumentProcessingService batchDocumentProcessingService;

    @Autowired
    private GeneratorTestData generatorTestData;

    @Autowired
    private Job importUserJob;

    @Test
    void addBatchDocument() {
        var countProcessingDocument = this.batchDocumentProcessingService.addBatchDocument();
        assertEquals(5, countProcessingDocument);
    }

    @Test
    void BatchDocumentTest() {
        var status = batchDocumentProcessingService.addBatchDocument();
        assertFalse(status.isUnsuccessful());
    }

    @Test
    void sendForApprovedDocument_happyPath_ReturnTrue() {
      var list = generatorTestData.generateDocumentsTestData(1000, Status.DRAFT.toString());
      assertEquals(1000, list.size());
    }

    @Test
    void getDocument_1000Id_Return1000Documents() {
        var list = generatorTestData.generateDocumentsTestData(1000, Status.DRAFT.toString());
        List<BatchProcessingItem> listDoc = this.batchDocumentProcessingService.getBatchDocument(list);
        assertEquals(1000, listDoc.size());
    }

    @Test
    void sendForApprovedDocument_ErrorPath_ReturnProcessingResult() {
        var list = generatorTestData.generateDocumentsTestData(10, Status.DRAFT.toString());

    }

}