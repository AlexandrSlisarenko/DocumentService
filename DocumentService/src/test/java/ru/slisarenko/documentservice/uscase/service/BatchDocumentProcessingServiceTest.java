package ru.slisarenko.documentservice.uscase.service;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.job.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.slisarenko.documentservice.config.BatchConfig;
import ru.slisarenko.documentservice.config.MyTestContainer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@Testcontainers
@Import({MyTestContainer.class, BatchConfig.class})
class BatchDocumentProcessingServiceTest {

    @Autowired
    private BatchDocumentProcessingService batchDocumentProcessingService;


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


}