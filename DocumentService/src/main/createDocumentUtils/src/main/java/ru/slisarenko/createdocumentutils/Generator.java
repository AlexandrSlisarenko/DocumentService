package ru.slisarenko.createdocumentutils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Generator {
    @Value("${folderForDocument}")
    private String docDirectory;
    @Value("${countDoc}")
    private int countDoc;
    private final ResourceLoader resourceLoader;


    public boolean generate() throws IOException {

        for (var i = 0; i < countDoc; i++) {
            var docName = "DocumentN" + randomNumber();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.docDirectory + File.separator
                                                                           + docName + ".txt"))) {
                writer.write(docName+":Author"+randomNumber()+":"+randomNumber());
            }
        }
        /*Resource resource = resourceLoader.getResource("file:" + this.docDirectory + File.separator
                                                       + docName + ".txt");
        if (resource instanceof WritableResource) {
            try (OutputStream os = ((WritableResource) resource).getOutputStream()) {
                os.write(content.getBytes(StandardCharsets.UTF_8));
            }
        } else {
            throw new IOException("Resource is not writable: " + filePath);
        }*/
        return true;
    }

    private String randomNumber() {
        Random random = new Random();
        return random.nextInt(1000) + "";
    }
}
