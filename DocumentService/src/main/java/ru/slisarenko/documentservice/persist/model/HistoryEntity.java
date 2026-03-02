package ru.slisarenko.documentservice.persist.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.slisarenko.documentservice.enums.Command;
import ru.slisarenko.documentservice.enums.Status;

@Entity
@Table(schema = "document_db", name = "history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "uuid", unique = true, nullable = false)
    private UUID uuid;

    @Column(name = "author_chang", nullable = false)
    private String authorChang;

    @Column(name = "command", nullable = false)
    @Enumerated(EnumType.STRING)
    private Command command;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "date_chang", nullable = false)
    private LocalDateTime changeTime;

    @Column(name = "comment")
    private String comment;
}
