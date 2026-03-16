package ru.slisarenko.documentservice.uscase.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.InvalidJobParametersException;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.launch.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.JobRestartException;
import org.springframework.stereotype.Service;
import ru.slisarenko.documentservice.enums.StatusBatchProcessing;
import ru.slisarenko.documentservice.uscase.dto.BatchProcessingItem;
import ru.slisarenko.documentservice.uscase.exception.DocumentNotFoundException;

import static ru.slisarenko.documentservice.uscase.utils.Constants.USER_APPROVER;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatchDocumentProcessingService {
    private final DocService docService;
    private JobOperator jobOperator;
    private Job job;

    public BatchStatus addBatchDocument() {
        JobParameters params = new JobParametersBuilder()
                .addString("JobID", String.valueOf(System.currentTimeMillis()))
                .toJobParameters();
        try {
            var resJob = jobOperator.start(job, params);
            return resJob.getStatus();
        } catch (JobInstanceAlreadyCompleteException | JobExecutionAlreadyRunningException |
                 InvalidJobParametersException | JobRestartException e) {
            throw new RuntimeException(e);
        }

    }

    public List<BatchProcessingItem> getBatchDocument(List<UUID> list) {
        int numberOfThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        var arrayInput = list.toArray();
        List<Future<List<BatchProcessingItem>>> futures = new ArrayList<>();
        List<BatchProcessingItem> result = new ArrayList<>();

        startBatchProcessing(numberOfThreads, arrayInput, futures, executorService);
        getResultBatchProcessing(futures, result);

        executorService.shutdown();

        return result;
    }

    private static void getResultBatchProcessing(List<Future<List<BatchProcessingItem>>> futures, List<BatchProcessingItem> result) {
        try {
            for (Future<List<BatchProcessingItem>> future : futures) {
                List<BatchProcessingItem> processedChunk = future.get(); // ждём завершения
                result.addAll(processedChunk);
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private void startBatchProcessing(int numberOfThreads,
                                      Object[] arrayInput,
                                      List<Future<List<BatchProcessingItem>>> futures,
                                      ExecutorService executorService) {
        var chunkSize = arrayInput.length / numberOfThreads;
        for (int i = 0; i < numberOfThreads; i++) {
            int start = i * chunkSize;
            int end = (i == numberOfThreads - 1) ? arrayInput.length : start + chunkSize;
            var chunk = Arrays.copyOfRange(arrayInput, start, end);

            Callable<List<BatchProcessingItem>> task = () -> {
                List<BatchProcessingItem> response = new ArrayList<>();
                for (Object o : chunk) {
                    var uuid = (UUID) o;
                    if (existsDocument(uuid)) {
                        var history = submittedDocument(uuid, USER_APPROVER);
                        var item = (history) ?
                                getBatchProcessingItem(uuid, StatusBatchProcessing.SUCCESSFULLY) :
                                getBatchProcessingItem(uuid, StatusBatchProcessing.CONFLICT);
                        response.add(item);
                    } else {
                        response.add(getBatchProcessingItem(uuid, StatusBatchProcessing.NOT_FOUND));
                    }
                }
                return response;
            };
            futures.add(executorService.submit(task));
        }
    }

    private boolean existsDocument(UUID uuid) {
        try {
            return docService.existsByUUID(uuid);
        } catch (DocumentNotFoundException e) {
            return false;
        }
    }

    private boolean submittedDocument(UUID uuidDoc, String approver) {
        try {
            docService.sendToSubmitted(uuidDoc, approver, "Package processing");
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    private BatchProcessingItem getBatchProcessingItem(UUID uuid, StatusBatchProcessing statusBatchProcessing) {
        return BatchProcessingItem.builder().id(uuid).statusBatchProcessing(statusBatchProcessing).build();
    }

    public int deleteDocuments(List<UUID> deleteList) {
        return this.docService.deleteDocuments(deleteList);
    }

    public int deleteHistoryDocuments(List<UUID> deleteHistoryDocuments) {
        return this.docService.deleteHistoryDocuments(deleteHistoryDocuments);
    }
}


