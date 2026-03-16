package ru.slisarenko.documentservice.persist.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.slisarenko.documentservice.enums.Status;
import ru.slisarenko.documentservice.persist.model.HistoryEntity;

public interface HistoryRepository extends JpaRepository<HistoryEntity, Long> {
    List<HistoryEntity> findAllByUuidOrderByChangeTimeAsc(UUID uuidDoc);

    @Modifying
    @Transactional
    @Query("DELETE FROM HistoryEntity history WHERE history.uuid IN :uuids")
    int deleteAllByUuidInBatch(@Param("uuids") List<UUID> deleteHistoryDocuments);

    boolean existsByUuidAndStatus(UUID uidDoc, Status status);
}

