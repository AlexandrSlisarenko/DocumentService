package ru.slisarenko.documentservice.uscase.service;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.slisarenko.documentservice.config.MyTestContainer;
import ru.slisarenko.documentservice.enums.Status;
import ru.slisarenko.documentservice.uscase.dto.DocumentFieldDTO;
import ru.slisarenko.documentservice.uscase.exception.EmptyAndLengthException;

@SpringBootTest
@Testcontainers
@Import(MyTestContainer.class)
class DocumentServiceTest {

    @Autowired
    private DocumentPersistentService documentService;

    @Test
    void save_NotNullDocument_ReturnSavingDocument() {
        var documentFields = DocumentFieldDTO.builder()
                .author("author")
                .name("name")
                .build();

        var documentFromDB  = this.documentService.createNewDocument(documentFields);
        Assertions.assertNotNull(documentFromDB);
        Assertions.assertNotNull(documentFromDB.getId());
        Assertions.assertNotNull(documentFromDB.getUuid());
        Assertions.assertNotNull(documentFromDB.getChangeTime());
        Assertions.assertEquals(documentFields.author(), documentFromDB.getAuthor());
        Assertions.assertEquals(documentFields.name(), documentFromDB.getName());
        Assertions.assertEquals(Status.DRAFT, documentFromDB.getStatus());
    }

    @Test
    void save_NotValidFieldName_ReturnException() {
        var documentFields = DocumentFieldDTO.builder()
                .author("author")
                .name("")
                .build();
        Assertions.assertThrows(EmptyAndLengthException.class,
                () -> this.documentService.createNewDocument(documentFields));
    }

    @Test
    void save_NotValidFieldAuthor_ReturnException() {
        var documentFields = DocumentFieldDTO.builder()
                .author("authorrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr" +
                        "rrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrreeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee" +
                        "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee" +
                        "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee")
                .name("name")
                .build();
        Assertions.assertThrows(EmptyAndLengthException.class,
                () -> this.documentService.createNewDocument(documentFields));
    }

}