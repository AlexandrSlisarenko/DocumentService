package ru.slisarenko.documentservice.persist.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "document_db", name = "document_data")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class DocumentDataEntity {
    @Id
    @Column(name = "uuid_doc")
    private UUID id;

    @Column(name = "text_doc", nullable = false)
    private String text;
}
