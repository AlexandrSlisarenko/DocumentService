package ru.slisarenko.documentservice.uscase.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import ru.slisarenko.documentservice.enums.Command;
import ru.slisarenko.documentservice.enums.Status;

@Builder
public record FilterDTO(String nameDocument,
                        String author,
                        String updateAuthor,
                        Status statusInHistory,
                        Status statusDocument,
                        LocalDateTime createTime,
                        LocalDateTime updateTime,
                        Command command,
                        String inComment,
                        int pageNumber,
                        int countElement) {

}
