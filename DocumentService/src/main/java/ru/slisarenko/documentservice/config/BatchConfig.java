package ru.slisarenko.documentservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfig {
    /*private JobBuilderFactory jobBuilderFactory;
    private StepBuilderFactory stepBuilderFactory;*/
}
