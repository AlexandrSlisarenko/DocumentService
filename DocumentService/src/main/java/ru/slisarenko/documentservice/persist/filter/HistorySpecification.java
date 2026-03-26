package ru.slisarenko.documentservice.persist.filter;

import jakarta.persistence.criteria.CriteriaBuilder;
import java.time.LocalDateTime;
import org.springframework.data.jpa.domain.Specification;
import ru.slisarenko.documentservice.enums.Command;
import ru.slisarenko.documentservice.enums.Status;
import ru.slisarenko.documentservice.persist.model.HistoryEntity;
import ru.slisarenko.documentservice.uscase.dto.FilterDTO;

public class HistorySpecification {
    public static Specification<HistoryEntity> filterUpdateDocumentBetween(LocalDateTime from, LocalDateTime to) {
        return (root, query, cb) -> {
            if ( from == null && to == null) return cb.conjunction();
            if (from == null) return cb.lessThanOrEqualTo(root.get("changeTime"), to);
            if (to == null) return cb.greaterThanOrEqualTo(root.get("changeTime"), from);
            return cb.between(root.get("birthDate"), from, to);
        };
    }

    public static Specification<HistoryEntity> filterEqualsAuthorUpdateDocument(String author) {
        return (root, query, cb) -> {
            return author == null ? cb.conjunction()
                    : cb.equal(root.get("authorChang"), author);
        };
    }

    public static Specification<HistoryEntity> filterEqualsCommand(Command command) {
        return (root, query, cb) -> {
            return command == null ? cb.conjunction()
                    : cb.equal(root.get("command"), command);
        };
    }

    public static Specification<HistoryEntity> filterLikeInComment(String comment) {
        return (root, query, cb) -> {
            if(comment == null) return cb.conjunction();
            var pattern = "%" + comment.toLowerCase() + "%";
            return cb.like(cb.lower(root.get("command")), pattern);
        };
    }

    public static Specification<HistoryEntity> filterEqualsStatuesDocumentInHistory(Status status) {
        return (root, query, cb) -> {
            return status == null ? cb.conjunction()
                    : cb.equal(root.get("status"), status);
        };
    }

    public static Specification<HistoryEntity> filterHistoryDocumentsByAllParam(FilterDTO filter) {
        return Specification.where(filterEqualsAuthorUpdateDocument(filter.updateAuthor()))
                .and(filterEqualsStatuesDocumentInHistory(filter.statusDocument()))
                .and(filterEqualsCommand(filter.command()))
                .and(filterLikeInComment(filter.inComment()))
                .and(filterUpdateDocumentBetween(filter.updateTimeFrom(), filter.updateTimeTo()));
    }
}
