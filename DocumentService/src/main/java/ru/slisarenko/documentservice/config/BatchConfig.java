package ru.slisarenko.documentservice.config;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.FlatFileItemWriter;
import org.springframework.batch.infrastructure.item.file.MultiResourceItemReader;
import org.springframework.batch.infrastructure.item.file.MultiResourceItemWriter;
import org.springframework.batch.infrastructure.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.infrastructure.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.infrastructure.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.infrastructure.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.infrastructure.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import ru.slisarenko.documentservice.uscase.dto.DocumentCreateFieldDTO;
import ru.slisarenko.documentservice.uscase.service.DocService;


@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfig {

    private final DocService docService;

    @Bean
    @StepScope
    public FlatFileItemReader<DocumentCreateFieldDTO> fileItemReader() {

        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer(":");
        tokenizer.setNames("name", "author", "text");

        BeanWrapperFieldSetMapper<DocumentCreateFieldDTO> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(DocumentCreateFieldDTO.class);

        DefaultLineMapper<DocumentCreateFieldDTO> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        lineMapper.afterPropertiesSet();

        return new FlatFileItemReader<>(lineMapper);
    }

    @Bean
    @StepScope
    public MultiResourceItemReader<DocumentCreateFieldDTO> multiResourceItemReaderNew(
            @Value("${folderForDocument:D:/Projects/DocumentService/DocumentService/DocumentDirectory/}") String path,
            FlatFileItemReader<DocumentCreateFieldDTO> fileItemReader) {
        var resources = getResources(path);
        MultiResourceItemReader<DocumentCreateFieldDTO> reader = new MultiResourceItemReader<>(fileItemReader);
        reader.setResources(resources);
        reader.setStrict(true);
        return reader;
    }

    @Bean
    public ItemProcessor<DocumentCreateFieldDTO, DocumentCreateFieldDTO> processor() {
        return document -> {
            var docFields = this.docService.createDocumentFieldDTO(document.getName(), document.getAuthor());
            var history = this.docService.createDocument(docFields, "Create");
            var dataDocument = this.docService.saveDocumentData(history.getUuid(),document.getText());
            System.out.println(document);
            System.out.println(history.getUuid().toString());
            document.setText(dataDocument.getId().toString());
            return document;
        };
    }

    @Bean
    public FlatFileItemWriter<DocumentCreateFieldDTO> fileItemWriter() {
        FlatFileItemWriter<DocumentCreateFieldDTO> writer = new FlatFileItemWriter<>(new DelimitedLineAggregator<>() {{
            setDelimiter(":");
            setFieldExtractor(new BeanWrapperFieldExtractor<>() {{
                setNames(new String[]{"name", "author", "text"});
            }});
        }});
        writer.setHeaderCallback(w -> w.write("name, author, uuid_doc"));
        return writer;
    }

    @Bean
    @StepScope
    public MultiResourceItemWriter<DocumentCreateFieldDTO> multiResourceItemWriterNew(
            @Value("${folderForDocumentResult:D:/Projects/DocumentService/DocumentService/DocumentDirectory/result/result.txt}") String outputDir,
            FlatFileItemWriter<DocumentCreateFieldDTO> fileItemWriter) {

        MultiResourceItemWriter<DocumentCreateFieldDTO> writer = new MultiResourceItemWriter<>(fileItemWriter);
        writer.setResource(new FileSystemResource(outputDir));
        writer.setItemCountLimitPerResource(100);
        writer.setResourceSuffixCreator(index -> "-" + index + ".txt");
        return writer;
    }

    @Bean
    public Step step(JobRepository jobRepository,
                     MultiResourceItemReader<DocumentCreateFieldDTO> multiResourceItemReaderNew,
                     ItemProcessor<DocumentCreateFieldDTO, DocumentCreateFieldDTO> processor,
                     MultiResourceItemWriter<DocumentCreateFieldDTO> multiResourceItemWriterNew) {
        return new StepBuilder("importStep", jobRepository)
                .<DocumentCreateFieldDTO, DocumentCreateFieldDTO>chunk(10)
                .reader(multiResourceItemReaderNew)
                .processor(processor)
                .writer(multiResourceItemWriterNew)
                .build();
    }


    @Bean
    public Job importDocumentsJob(JobRepository jobRepository, Step step) {
        return new JobBuilder("importDocumentsJob", jobRepository)
                .start(step)
                .build();
    }


    private Resource[] getResources(String path) {
        try {
            Path dir = Paths.get(path);

            try (var pathStream = Files.list(dir)) {
                return pathStream
                        .filter(Files::isRegularFile)
                        .map(FileSystemResource::new)
                        .toArray(Resource[]::new);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
