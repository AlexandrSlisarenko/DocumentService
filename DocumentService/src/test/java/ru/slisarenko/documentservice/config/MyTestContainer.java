package ru.slisarenko.documentservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.postgresql.PostgreSQLContainer;

@TestConfiguration(proxyBeanMethods = false)
public class MyTestContainer {

    private static final Logger log = LoggerFactory.getLogger(MyTestContainer.class);


    @Bean
    @ServiceConnection
    public PostgreSQLContainer postgresContainer() {
        return new PostgreSQLContainer("postgres:17-alpine")
                .withDatabaseName("document_db")
                .withUsername("postgres")
                .withPassword("postgres")
                /*.withCommand("postgres",
                        "-c", "log_statement=INFO",
                        "-c", "log_min_duration_statement=0")*/
                .withLogConsumer(new Slf4jLogConsumer(log).withPrefix("POSTGRES"));
    }

}
