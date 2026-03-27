package ru.slisarenko.documentservice.persist.filter;

import java.time.LocalDateTime;
import org.springframework.data.jpa.domain.Specification;
import ru.slisarenko.documentservice.enums.Status;
import ru.slisarenko.documentservice.persist.model.DocumentEntity;
import ru.slisarenko.documentservice.persist.model.DocumentEntity_;
import ru.slisarenko.documentservice.uscase.dto.FilterDTO;

public class DocumentSpecification {

    public static Specification<DocumentEntity> filterCreateDocumentBetween(LocalDateTime from, LocalDateTime to) {
        return (root, query, cb) -> {
            if ( from == null && to == null) return cb.conjunction();
            if (from == null) return cb.lessThanOrEqualTo(root.get(DocumentEntity_.CHANGE_TIME), to);
            if (to == null) return cb.greaterThanOrEqualTo(root.get(DocumentEntity_.CHANGE_TIME), from);
            return cb.between(root.get(DocumentEntity_.CHANGE_TIME), from, to);
        };
    }



    public static Specification<DocumentEntity> filterEqualsNameDocument(String name) {
        return (root, query, cb) -> {
            return name == null ? cb.conjunction()
                    : cb.equal(root.get(DocumentEntity_.NAME), name);
        };
    }

    public static Specification<DocumentEntity> filterEqualsAuthorDocument(String author) {
        return (root, query, cb) -> {
            return author == null ? cb.conjunction()
                    : cb.equal(root.get(DocumentEntity_.AUTHOR), author);
        };
    }



    public static Specification<DocumentEntity> filterEqualsStatuesDocument(Status status) {
        return (root, query, cb) -> {
            return status == null ? cb.conjunction()
                    : cb.equal(root.get(DocumentEntity_.STATUS), status);
        };
    }


    public static Specification<DocumentEntity> filterDocumentsByAllParam(FilterDTO filter) {
        return Specification.where(filterEqualsNameDocument(filter.nameDocument()))
                .and(filterEqualsAuthorDocument(filter.author()))
                .and(filterEqualsStatuesDocument(filter.statusDocument()))
                .and(filterCreateDocumentBetween(filter.createTimeFrom(), filter.createTimeTo()));
    }


}

