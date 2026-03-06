package ru.slisarenko.documentservice.persist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.slisarenko.documentservice.persist.model.ApprovalRegisterEntity;

public interface ApprovalRegisterRepository extends JpaRepository<ApprovalRegisterEntity, Long> {
}
