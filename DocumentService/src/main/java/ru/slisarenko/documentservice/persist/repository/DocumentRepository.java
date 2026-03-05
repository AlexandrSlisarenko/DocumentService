package ru.slisarenko.documentservice.persist.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.slisarenko.documentservice.persist.model.DocumentEntity;

public interface DocumentRepository extends JpaRepository<DocumentEntity, Long> {
    DocumentEntity findByUuid(UUID uuid);
}
