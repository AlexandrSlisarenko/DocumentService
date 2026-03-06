package ru.slisarenko.documentservice.persist.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.slisarenko.documentservice.persist.model.DocumentEntity;

public interface DocumentRepository extends JpaRepository<DocumentEntity, Long> {
    DocumentEntity findByUuid(UUID uuid);

    @Query("SELECT doc FROM DocumentEntity doc WHERE doc.uuid IN :ids")
    Page<DocumentEntity> findDocumentEntitiesByIds(@Param("ids") List<UUID> ids, Pageable pageable);
}
