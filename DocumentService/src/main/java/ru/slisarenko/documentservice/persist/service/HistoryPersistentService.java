package ru.slisarenko.documentservice.persist.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;
import org.springframework.stereotype.Service;
import ru.slisarenko.documentservice.enums.Command;
import ru.slisarenko.documentservice.enums.Status;
import ru.slisarenko.documentservice.persist.filter.HistorySpecification;
import ru.slisarenko.documentservice.persist.mapper.FilterMapper;
import ru.slisarenko.documentservice.persist.model.DocumentEntity;
import ru.slisarenko.documentservice.persist.model.HistoryEntity;
import ru.slisarenko.documentservice.persist.repository.HistoryRepository;
import ru.slisarenko.documentservice.uscase.dto.FilterDTO;
import ru.slisarenko.documentservice.uscase.dto.HistoryFieldDTO;
import ru.slisarenko.documentservice.uscase.exception.EmptyAndLengthException;
import ru.slisarenko.documentservice.uscase.exception.ErrorSavingDataException;
import ru.slisarenko.documentservice.uscase.exception.HistoryElementNotFoundException;
import ru.slisarenko.documentservice.uscase.utils.CheckField;

@Service
@RequiredArgsConstructor
public class HistoryPersistentService {
    private final HistoryRepository historyRepository;
    private final FilterMapper filterMapper;

    public HistoryEntity saveHistoryDocument(HistoryFieldDTO historyFieldDTO) {
        var historyEntity = createHistoryEntity(historyFieldDTO);
        try {
            var historyIdFormDB = this.historyRepository.save(historyEntity).getId();
            return this.historyRepository.findById(historyIdFormDB)
                                                        .orElseThrow(() -> new HistoryElementNotFoundException("History id = " + historyIdFormDB));
        } catch (Exception e) {
            throw new ErrorSavingDataException(e.getMessage());
        }
    }
    public HistoryEntity saveHistoryDocument(DocumentEntity document, String userName, Command command, String comment){
        var historyDTO = createHistoryField(document, userName, command, comment);
        return saveHistoryDocument(historyDTO);
    }
    private HistoryFieldDTO createHistoryField(DocumentEntity document, String userName, Command command, String comment) {
        return HistoryFieldDTO.builder()
                .comment(comment)
                .uuid(document.getUuid())
                .status(document.getStatus().toString())
                .command(command.toString())
                .authorChang(userName)
                .changeTime(document.getChangeTime())
                .build();
    }

    private HistoryEntity createHistoryEntity(HistoryFieldDTO historyFieldDTO){
        checkHistoryFields(historyFieldDTO);
        return HistoryEntity.builder()
                .comment(historyFieldDTO.comment())
                .command(Command.valueOf(historyFieldDTO.command()))
                .authorChang(historyFieldDTO.authorChang())
                .status(Status.valueOf(historyFieldDTO.status()))
                .changeTime(historyFieldDTO.changeTime())
                .uuid(historyFieldDTO.uuid())
                .build();
    }

    private void checkHistoryFields(HistoryFieldDTO historyFieldDTO){
        if (!CheckField.checkCommand(historyFieldDTO.command().toString())){
            throw new EmptyAndLengthException("Command");
        }
    }


    public List<HistoryEntity> getAllHistoryByUuid(UUID uuidDoc) {
        return this.historyRepository.findAllByUuidOrderByChangeTimeAsc(uuidDoc);
    }

    public int deleteBatch(List<UUID> deleteHistoryDocuments) {
        return this.historyRepository.deleteAllByUuidInBatch(deleteHistoryDocuments);
    }

    public boolean existsActualHistory(UUID uidDoc, Status status) {
        return this.historyRepository.existsByUuidAndStatus(uidDoc, status);
    }

    public List<UUID> findDocumentUuidByFilter(FilterDTO filter) {
        var paramFilter = this.filterMapper.toHistoryEntity(filter);
        var matcherHistory = ExampleMatcher.matching()
                .withIgnoreNullValues()
                .withIgnorePaths("id")
                .withMatcher("authorChang", matcher -> matcher.contains().ignoreCase())
                .withMatcher("command", matcher -> matcher.exact().ignoreCase())
                .withMatcher("status", matcher -> matcher.exact().ignoreCase())
                .withMatcher("comment", matcher -> matcher.contains().ignoreCase());
        Example<HistoryEntity> example = Example.of(paramFilter, matcherHistory);
        return this.historyRepository.findBy(example, FetchableFluentQuery::all)
                .stream().distinct().map(HistoryEntity::getUuid).toList();
    }

    public List<HistoryEntity> searchDocumentsHistoryBySpecification(FilterDTO criteria) {
        Specification<HistoryEntity> spec = Specification.where(HistorySpecification.filterHistoryDocumentsByAllParam(criteria)); // начальная спецификация

        return historyRepository.findAll(spec);
    }
}
