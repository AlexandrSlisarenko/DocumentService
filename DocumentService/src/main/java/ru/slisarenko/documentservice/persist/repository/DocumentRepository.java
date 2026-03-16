package ru.slisarenko.documentservice.persist.repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.slisarenko.documentservice.enums.Status;
import ru.slisarenko.documentservice.persist.model.DocumentEntity;

public interface DocumentRepository extends JpaRepository<DocumentEntity, Long> {
    DocumentEntity findByUuid(UUID uuid);

    @Query("SELECT doc FROM DocumentEntity doc WHERE doc.uuid IN :ids")
    Page<DocumentEntity> findDocumentEntitiesByIds(@Param("ids") List<UUID> ids, Pageable pageable);

    List<DocumentEntity> findAllByStatus(Status status, Limit limit);

    @Query("SELECT doc.uuid FROM DocumentEntity doc WHERE doc.status = :status")
    List<UUID> findUuidListByStatus(Status status, Limit limit);

    @Modifying
    @Transactional
    @Query("DELETE FROM DocumentEntity document where document.uuid IN :uuids")
    int deleteAllByUuidBatch(@Param("uuids") Collection<UUID> uuids);

    boolean existsByUuid(UUID uuid);
}
