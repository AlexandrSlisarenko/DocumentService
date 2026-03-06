package ru.slisarenko.createdocumentutils;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CreateDocumentUtilsApplicationTests {

    @Autowired
    private Generator generator;

    @Test
    void generateDocument_five_ReturnFiveDocument() throws IOException {
        Assertions.assertTrue(this.generator.generate());
    }

}
