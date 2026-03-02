package ru.slisarenko.documentservice.persist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.slisarenko.documentservice.persist.model.DocumentEntity;

public interface DocumentRepository extends JpaRepository<DocumentEntity, Long> {
}
