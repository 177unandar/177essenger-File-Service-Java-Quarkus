package com.github._177unandar._177essenger.file_service.core.domain.usecases;

import com.github._177unandar._177essenger.file_service.core.contracts.FileFolders;
import com.github._177unandar._177essenger.file_service.core.contracts.FilePurposes;
import com.github._177unandar._177essenger.file_service.core.domain.entities.FileEntity;
import com.github._177unandar._177essenger.file_service.core.domain.exceptions.UploadFileException;
import com.github._177unandar._177essenger.file_service.core.services.FileService;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class UploadImageProfileUseCaseImpl implements UploadImageProfileUseCase {

    private final FileService fileService;

    public UploadImageProfileUseCaseImpl(FileService fileService) {
        this.fileService = fileService;
    }

    @Override
    public String execute(byte[] file, String filename, UUID userId) throws UploadFileException {
        FileEntity fileEntity = fileService.uploadFile(file, filename, FileFolders.IMAGE_PROFILE, FilePurposes.IMAGE_PROFILE, userId, null);
        return fileEntity.getId();
    }
}
