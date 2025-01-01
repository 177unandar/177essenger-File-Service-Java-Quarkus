package com.github._177unandar._177essenger.file_service.core.services;

import com.github._177unandar._177essenger.file_service.core.contracts.ErrorCodes;
import com.github._177unandar._177essenger.file_service.core.contracts.FileService;
import com.github._177unandar._177essenger.file_service.core.domain.entities.FileEntity;
import com.github._177unandar._177essenger.file_service.core.domain.exceptions.FileAccessDeniedException;
import com.github._177unandar._177essenger.file_service.core.domain.exceptions.FileNotFoundException;
import com.github._177unandar._177essenger.file_service.core.domain.exceptions.UploadFileException;
import com.github._177unandar._177essenger.file_service.core.domain.repositories.FileRepository;
import com.github._177unandar._177essenger.file_service.core.storages.FileStorage;
import com.google.gson.Gson;
import io.imagekit.sdk.exceptions.*;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class FileServiceImpl implements FileService {
    private final FileRepository fileRepository;
    private final FileStorage fileStorage;
    private final Gson gson;

    public FileServiceImpl(FileRepository fileRepository, FileStorage fileStorage, Gson gson) {
        this.fileRepository = fileRepository;
        this.fileStorage = fileStorage;
        this.gson = gson;
    }

    @Override
    public FileEntity uploadFile(byte[] file, String filename, String folder, String purpose, UUID uploadedBy, List<UUID> permissions) throws UploadFileException {
        boolean isPrivate = permissions != null;
        Object fileData = fileStorage.uploadFile(file, filename, folder, isPrivate);
        return fileRepository.save(FileEntity.builder()
                .provider(fileStorage.getProvider())
                .data(gson.toJson(fileData))
                .uploadedBy(uploadedBy)
                .permission(permissions)
                .build());
    }

    @Override
    public String getFileUrl(String fileId) throws FileNotFoundException {
        return getFileUrl(fileId, null, null);
    }

    @Override
    public String getFileUrl(String fileId, String width, String height) throws FileNotFoundException {
        FileEntity fileEntity = fileRepository.findById(fileId).orElseThrow(() -> new FileNotFoundException(ErrorCodes.FILE_NOT_FOUND, this.getClass().getSimpleName(), "File with id " + fileId + " not found."));
        return fileStorage.getFileUrl(fileEntity.getData(), width, height);
    }

    @Override
    public String getFileSignedUrl(String fileId, UUID userId, int expireInSeconds) throws FileNotFoundException, FileAccessDeniedException {
        return getFileSignedUrl(fileId, userId, expireInSeconds, null, null);
    }

    @Override
    public String getFileSignedUrl(String fileId, UUID userId, int expireInSeconds, String width, String height) throws FileNotFoundException, FileAccessDeniedException {
        FileEntity fileEntity = fileRepository.findById(fileId).orElseThrow(() -> new FileNotFoundException(ErrorCodes.FILE_NOT_FOUND, this.getClass().getSimpleName(), "File with id " + fileId + " not found."));
        List<UUID> permissions = fileEntity.getPermission();
        if (permissions != null && permissions.contains(userId)) {
            return fileStorage.generateSignedUrl(fileEntity.getData(), expireInSeconds, width, height);
        }
        throw new FileAccessDeniedException(ErrorCodes.ACCESS_DENIED, this.getClass().getSimpleName(), "Access denied");
    }

    @Override
    public boolean deleteFile(String fileId, UUID userId) throws ForbiddenException, TooManyRequestsException, InternalServerException, UnauthorizedException, BadRequestException, UnknownException, FileNotFoundException, FileAccessDeniedException {
        FileEntity fileEntity = fileRepository.findById(fileId).orElseThrow(() -> new FileNotFoundException(ErrorCodes.FILE_NOT_FOUND, this.getClass().getSimpleName(), "File with id " + fileId + " not found."));
        List<UUID> permissions = fileEntity.getPermission();
        if (permissions != null && permissions.contains(userId)) {
            return fileStorage.deleteFile(fileEntity.getData());
        }
        throw new FileAccessDeniedException(ErrorCodes.ACCESS_DENIED, this.getClass().getSimpleName(), "Access denied");
    }
}
