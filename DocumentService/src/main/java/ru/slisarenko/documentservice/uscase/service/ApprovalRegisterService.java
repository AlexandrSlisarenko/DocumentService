package ru.slisarenko.documentservice.uscase.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.slisarenko.documentservice.persist.model.ApprovalRegisterEntity;
import ru.slisarenko.documentservice.persist.repository.ApprovalRegisterRepository;
import ru.slisarenko.documentservice.uscase.exception.DocumentIsApprovedException;

@Service
@RequiredArgsConstructor
public class ApprovalRegisterService {
    private final ApprovalRegisterRepository approvalRegisterRepository;

    public boolean addDocument(UUID documentUUID) {
        var registerRecord = ApprovalRegisterEntity.builder()
                .uuid(documentUUID)
                .build();
        try {
            this.approvalRegisterRepository.save(registerRecord);
        }catch (Exception e) {
            throw new DocumentIsApprovedException(e.getMessage());
        }
        return true;
    }
}
