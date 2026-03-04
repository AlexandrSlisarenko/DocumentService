package ru.slisarenko.documentservice.uscase.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.slisarenko.documentservice.enums.Command;
import ru.slisarenko.documentservice.enums.Status;
import ru.slisarenko.documentservice.persist.model.HistoryEntity;
import ru.slisarenko.documentservice.persist.repository.HistoryRepository;
import ru.slisarenko.documentservice.uscase.dto.HistoryFieldDTO;
import ru.slisarenko.documentservice.uscase.exception.EmptyAndLengthException;
import ru.slisarenko.documentservice.uscase.exception.ErrorSavingDataException;
import ru.slisarenko.documentservice.uscase.exception.HistoryElementNotFoundException;
import ru.slisarenko.documentservice.uscase.utils.CheckField;

@Service
@RequiredArgsConstructor
public class HistoryPersistentService {
    private final HistoryRepository historyRepository;

    public HistoryEntity saveHistoryDocument(HistoryFieldDTO historyFieldDTO) {
        var historyEntity = createHistoryEntity(historyFieldDTO);
        try {
            var historyIdFormDB = this.historyRepository.save(historyEntity).getId();
            return this.historyRepository.findById(historyIdFormDB)
                                                        .orElseThrow(HistoryElementNotFoundException::new);
        } catch (Exception e) {
            throw new ErrorSavingDataException(e.getMessage());
        }
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


}
