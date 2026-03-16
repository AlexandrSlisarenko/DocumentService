package ru.slisarenko.documentservice.persist.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.slisarenko.documentservice.persist.model.DocumentDataEntity;

public interface DocumentDataRepository extends JpaRepository<DocumentDataEntity, UUID> {
}
