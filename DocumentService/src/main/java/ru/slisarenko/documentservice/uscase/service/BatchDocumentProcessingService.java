package ru.slisarenko.documentservice.uscase.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.infrastructure.item.file.MultiResourceItemReader;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;
import ru.slisarenko.documentservice.uscase.dto.DocumentFieldDTO;
import ru.slisarenko.documentservice.uscase.exception.DocumentErrorReadException;
import ru.slisarenko.documentservice.uscase.exception.DocumentFormatException;
import ru.slisarenko.documentservice.uscase.exception.DocumentNotReadableException;

@Service
@RequiredArgsConstructor
public class BatchDocumentProcessingService {
    private DocService docService;
    private final ResourcePatternResolver resourceLoader;

    public int addBatchDocument(String path) {
        MultiResourceItemReader<String> reader = new MultiResourceItemReader<>();
        int count = 0;
        try {
            var resources = new PathMatchingResourcePatternResolver()
                    .getResources("file:" + path + "\\*.txt");
            reader.setResources(resources);
            reader.read();
            /*for (Resource resource : reader.) {

                if (resource instanceof InputStreamResource) {
                    try (BufferedReader bufferedReader = new BufferedReader(new FileReader(resource.getFile()))) {
                        var document = bufferedReader.readLine().split(":");
                        var fieldDTO = createDocumentFieldDTO(document[0], document[1]);
                        this.docService.createDocument(fieldDTO,"");
                        count++;
                    } catch (IOException e) {
                        throw new DocumentFormatException(e.getMessage());
                    }
                } else {
                    throw new DocumentNotReadableException(resource.getFilename());
                }
            }*/
        } catch (IOException e) {
            throw new DocumentErrorReadException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return count;
    }


    private DocumentFieldDTO createDocumentFieldDTO(String name, String author) {
        return DocumentFieldDTO.builder()
                .author(author)
                .name(name)
                .build();
    }
}
