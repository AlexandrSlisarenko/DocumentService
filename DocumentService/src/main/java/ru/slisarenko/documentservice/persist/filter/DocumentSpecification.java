package ru.slisarenko.documentservice.persist.filter;

import org.springframework.data.jpa.domain.Specification;
import ru.slisarenko.documentservice.persist.model.DocumentEntity;
import ru.slisarenko.documentservice.uscase.dto.FilterDTO;

public class DocumentSpecification {

    public static Specification<DocumentEntity> filter(FilterDTO filter) {
        return null;
    }
}
