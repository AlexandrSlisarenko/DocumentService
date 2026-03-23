package ru.slisarenko.documentservice.uscase.dto;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import lombok.Builder;
import ru.slisarenko.documentservice.enums.Status;

@Builder
public record BatchProcessingInputElements(int numberOfThreads,
                                           Object[] arrayInput,
                                           List<Future<List<BatchProcessingItem>>> futures,
                                           ExecutorService executorService,
                                           Status status,
                                           List<BatchProcessingItem> result) {
}
