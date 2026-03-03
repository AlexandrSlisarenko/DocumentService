package ru.slisarenko.documentservice.persist.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "document_db", name = "approval_register")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalRegisterEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid_doc", unique = true, nullable = false)
    private UUID uuid;
}
