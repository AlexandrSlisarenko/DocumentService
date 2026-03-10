package ru.slisarenko.documentservice.uscase.service;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.job.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.slisarenko.documentservice.config.BatchConfig;
import ru.slisarenko.documentservice.config.MyTestContainer;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@Import({MyTestContainer.class, BatchConfig.class})
class BatchDocumentProcessingServiceTest {
    public static final String TEST_PATH = "D:\\Projects\\DocumentService\\DocumentService\\src\\main" +
                                           "\\createDocumentUtils\\src\\main\\resources\\DocumentDirectory";


    @Autowired
    private BatchDocumentProcessingService batchDocumentProcessingService;

    @Autowired
    private Job importUserJob;

    @Test
    void addBatchDocument() {
        var countProcessingDocument = this.batchDocumentProcessingService.addBatchDocument(TEST_PATH);
        assertEquals(5, countProcessingDocument);
    }

    @Test
    void BatchDocumentTest() {
        batchDocumentProcessingService.addBatchDocument(TEST_PATH);
    }
}