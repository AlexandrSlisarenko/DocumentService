package ru.slisarenko.documentservice.service;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.slisarenko.documentservice.config.MyTestContainer;
import ru.slisarenko.documentservice.enums.Status;
import ru.slisarenko.documentservice.persist.repository.DocumentRepository;
import ru.slisarenko.documentservice.uscase.dto.DocumentFieldDTO;
import ru.slisarenko.documentservice.uscase.service.DocumentService;

@SpringBootTest
@Testcontainers
@Import(MyTestContainer.class)
class DocumentServiceTest {

    @Autowired
    private DocumentService documentService;
    private DocumentFieldDTO documentFields;

    DocumentServiceTest() {
        this.documentFields = DocumentFieldDTO.builder()
                .author("author")
                .name("name")
                .build();

    }

    @Test
    void save_NotNullDocument_ReturnSavingDocument() {
        var documentFromDB  = this.documentService.createNewDocument(this.documentFields);
        Assertions.assertNotNull(documentFromDB);
        Assertions.assertNotNull(documentFromDB.getId());
        Assertions.assertNotNull(documentFromDB.getUuid());
        Assertions.assertNotNull(documentFromDB.getChangeTime());
        Assertions.assertEquals(this.documentFields.author(), documentFromDB.getAuthor());
        Assertions.assertEquals(this.documentFields.name(), documentFromDB.getName());
        Assertions.assertEquals(Status.DRAFT, documentFromDB.getStatus());
    }



}