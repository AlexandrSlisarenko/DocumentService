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
import ru.slisarenko.documentservice.persist.service.HistoryPersistentService;
import ru.slisarenko.documentservice.uscase.dto.HistoryFieldDTO;

@SpringBootTest
@Testcontainers
@Import(MyTestContainer.class)
class HistoryPersistentServiceTest {

    private static final Long DOCUMENT_ID = 1L;
    private static final UUID UUID_ID = UUID.fromString("12ad5948-3457-45bd-b23a-659ab42bf75f");
    private static final LocalDateTime CREATE_DATE = LocalDateTime.of(2026,3,3,16,59, 8);
    private static final String AUTHOR_DOC = "AUTHOR_DOC";
    private static final String CREATE_STATUS = "DRAFT";
    private static final String CREATE_COMMAND = "Create";

    @Autowired
    private HistoryPersistentService historyPersistentService;
    private HistoryFieldDTO history;

    HistoryPersistentServiceTest() {
        this.history = HistoryFieldDTO.builder()
                .comment("comment")
                .uuid(UUID_ID)
                .authorChang(AUTHOR_DOC)
                .changeTime(CREATE_DATE)
                .command(CREATE_COMMAND)
                .status(CREATE_STATUS)
                .build();
    }

    @Test
    void saveHistoryDocument_NotNullDocument_ReturnIsTrue() {
        var historyFromDB = historyPersistentService.saveHistoryDocument(this.history);
        Assertions.assertNotNull(historyFromDB);
        Assertions.assertEquals(DOCUMENT_ID, historyFromDB.getId());
        Assertions.assertEquals(UUID_ID, historyFromDB.getUuid());
        Assertions.assertEquals(CREATE_DATE, historyFromDB.getChangeTime());
        Assertions.assertEquals(this.history.command(), historyFromDB.getCommand().toString());
        Assertions.assertEquals(this.history.comment(), historyFromDB.getComment());
        Assertions.assertEquals(this.history.status(), historyFromDB.getStatus().toString());
        Assertions.assertEquals(this.history.authorChang(), historyFromDB.getAuthorChang());
    }




}