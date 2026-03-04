package ru.slisarenko.documentservice.uscase.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DocService {

    private final DocumentPersistentService documentPersistentService;

    public boolean createDocument() {
        return false;
    }

}
