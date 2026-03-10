package ru.slisarenko.documentservice.uscase.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentCreateFieldDTO {
    private String name;
    private String author;
    private String text;
}
