package ru.slisarenko.documentservice.persist.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.slisarenko.documentservice.config.MyTestContainer;
import ru.slisarenko.documentservice.uscase.dto.FilterDTO;

import static ru.slisarenko.documentservice.uscase.utils.Constants.USER_CREATOR;

@SpringBootTest
@Testcontainers
@Import(MyTestContainer.class)
class HistoryPersistentServiceTest {

    @Autowired
    private HistoryPersistentService historyPersistentService;

    @Test
    void searchDocumentsHistoryBySpecification_EmptyData_ReturnsEmptyList() {
        var filter = FilterDTO.builder()
                .author(USER_CREATOR)
                .build();
        var result = historyPersistentService.searchDocumentsHistoryBySpecification(filter);
        Assertions.assertTrue(result.isEmpty());
    }
}