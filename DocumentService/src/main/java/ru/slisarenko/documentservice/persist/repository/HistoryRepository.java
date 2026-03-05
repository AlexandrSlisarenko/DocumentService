package ru.slisarenko.documentservice.persist.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.slisarenko.documentservice.persist.model.HistoryEntity;

public interface HistoryRepository extends JpaRepository<HistoryEntity, Long> {
    List<HistoryEntity> findAllByUuidOrderByChangeTimeAsc(UUID uuidDoc);
}

