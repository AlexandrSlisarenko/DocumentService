package ru.slisarenko.documentservice.persist.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.slisarenko.documentservice.config.MyTestContainer;
import ru.slisarenko.documentservice.uscase.dto.FilterDTO;

import static ru.slisarenko.documentservice.uscase.utils.Constants.USER_APPROVER;

@SpringBootTest
@Testcontainers
@Import(MyTestContainer.class)
class DocumentPersistentServiceTest {

    @Autowired
    private DocumentPersistentService documentPersistentService;

    @Test
    void searchDocumentsBySpecification_EmptyData_ReturnsEmptyList() {
        var filter = FilterDTO.builder()
                .updateAuthor(USER_APPROVER)
                .build();
        var result = documentPersistentService.searchDocumentsBySpecification(filter);
        Assertions.assertTrue(result.isEmpty());
    }
}