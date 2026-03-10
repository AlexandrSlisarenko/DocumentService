package ru.slisarenko.documentservice.config;


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
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.infrastructure.item.file.builder.MultiResourceItemReaderBuilder;
import org.springframework.batch.infrastructure.item.file.builder.MultiResourceItemWriterBuilder;
import org.springframework.batch.infrastructure.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.infrastructure.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.infrastructure.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import ru.slisarenko.documentservice.uscase.dto.DocumentCreateFieldDTO;
import ru.slisarenko.documentservice.uscase.service.DocService;


@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfig {

    @Value("${folderForDocument}")
    private String docDirectory;

    private final DocService docService;

    @Bean
    @StepScope
    public FlatFileItemReader<DocumentCreateFieldDTO> reader() {
        return new FlatFileItemReaderBuilder<DocumentCreateFieldDTO>()
                .name("DocumentName")
                //.resource(new ClassPathResource("DocumentN179.txt"))
                .delimited().delimiter(":")
                .names("name", "author", "text")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                    setTargetType(DocumentCreateFieldDTO.class);
                }})
                .build();
    }

    @Bean
    @StepScope
    public MultiResourceItemReader<DocumentCreateFieldDTO> multiResourceReader(@Value("${folderForDocument}\\*.txt") Resource[] resources) {
        return new MultiResourceItemReaderBuilder<DocumentCreateFieldDTO>()
                .delegate(reader())
                .resources(resources)
                .build();
    }

    @Bean
    public ItemProcessor<DocumentCreateFieldDTO, DocumentCreateFieldDTO> processor() {
        return person -> {
            var docFields = this.docService.createDocumentFieldDTO(person.getName(), person.getAuthor());
            var history = this.docService.createDocument(docFields, "Create");
            // Example transformation: uppercase names
            person.setName(person.getName().toUpperCase());
            person.setAuthor(person.getAuthor().toUpperCase());
            person.setText(history.getUuid().toString());
            return person;
        };
    }

    @Bean
    @StepScope
    public FlatFileItemWriter<DocumentCreateFieldDTO> writer() {
        return new FlatFileItemWriterBuilder<DocumentCreateFieldDTO>()
                .name("personItemWriter")
                //.resource(new FileSystemResource(outputFile))
                .lineAggregator(new DelimitedLineAggregator<DocumentCreateFieldDTO>() {{
                    setDelimiter(":");
                    setFieldExtractor(new BeanWrapperFieldExtractor<DocumentCreateFieldDTO>() {{
                        setNames(new String[]{"name", "author", "text"});
                    }});
                }})
                .headerCallback(writer -> writer.write("name, author, text"))
                .build();
    }

    @Bean
    @StepScope
    public MultiResourceItemWriter<DocumentCreateFieldDTO>multiResourceItemWriter(@Value("${folderForDocument}\\result\\*.txt") Resource resources) {
        return new MultiResourceItemWriterBuilder<DocumentCreateFieldDTO>()
                .delegate(writer())
                .resource(resources)
                .build();
    }

    @Bean
    public Step step(JobRepository jobRepository,
                     MultiResourceItemReader<DocumentCreateFieldDTO> multiResourceReader,
                     ItemProcessor<DocumentCreateFieldDTO, DocumentCreateFieldDTO> processor,
                     MultiResourceItemWriter<DocumentCreateFieldDTO>multiResourceItemWriter) {
        return new StepBuilder("importStep", jobRepository)
                .<DocumentCreateFieldDTO, DocumentCreateFieldDTO>chunk(10)
                .reader(multiResourceReader)
                .processor(processor)
                .writer(multiResourceItemWriter)
                .build();
    }

    @Bean
    public Job importDocumentsJob(JobRepository jobRepository, Step step) {
        return new JobBuilder("importDocumentsJob", jobRepository)
                .start(step)
                .build();
    }
}
