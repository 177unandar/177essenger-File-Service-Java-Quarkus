package com.github._177unandar._177essenger.file_service;

import com.github._177unandar._177essenger.file_service.adapter.out.repositories.MongoFileRepository;
import com.github._177unandar._177essenger.file_service.adapter.out.storages.ImageKitFileStorage;
import com.github._177unandar._177essenger.file_service.core.contracts.FilePurposes;
import com.github._177unandar._177essenger.file_service.core.contracts.FileService;
import com.github._177unandar._177essenger.file_service.core.contracts.StorageProviders;
import com.github._177unandar._177essenger.file_service.core.domain.entities.FileEntity;
import com.github._177unandar._177essenger.file_service.core.domain.exceptions.FileAccessDeniedException;
import com.github._177unandar._177essenger.file_service.core.domain.exceptions.FileNotFoundException;
import com.github._177unandar._177essenger.file_service.core.domain.repositories.FileRepository;
import com.github._177unandar._177essenger.file_service.core.services.FileServiceImpl;
import com.github._177unandar._177essenger.file_service.core.storages.FileStorage;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FileServiceTests {

    private final Gson gson = new Gson();
    private final FileStorage fileStorage;
    private final FileRepository fileRepository;
    private final FileService fileService;

    public FileServiceTests() {
        this.fileStorage = mock(ImageKitFileStorage.class);
        this.fileRepository = mock(MongoFileRepository.class);
        this.fileService = new FileServiceImpl(fileRepository, fileStorage, gson);

    }


    private final String storageEndPoint = System.getenv("IMAGEKIT_URL_ENDPOINT");
    private final String sampleImage = System.getenv("IMAGEKIT_SAMPLE_IMAGE");
    private final String samplePrivateImage = System.getenv("IMAGEKIT_SAMPLE_PRIVATE_IMAGE");

    private final FileStorageTestEntity uploadedPrivateFile = new FileStorageTestEntity(storageEndPoint, samplePrivateImage);
    private final String fileId = "1";
    private final String invalidFileId = "404";
    private final UUID authorizedUserId = UUID.randomUUID();
    private final UUID unauthorizedUserId = UUID.randomUUID();
    private final String resize = "100";
    private final int expiredInSeconds = 60;

    FileStorageTestEntity uploadedFile = new FileStorageTestEntity(storageEndPoint, sampleImage);

    FileEntity uploadedFileEntity = FileEntity.builder()
            .id(fileId)
            .provider(StorageProviders.IMAGEKIT)
            .data(gson.toJson(uploadedFile))
            .uploadedBy(authorizedUserId)
            .build();

    private final FileEntity uploadedPrivateFileEntity = FileEntity.builder()
            .id(fileId)
            .provider(StorageProviders.IMAGEKIT)
            .data(gson.toJson(uploadedPrivateFile))
            .uploadedBy(authorizedUserId)
            .permission(List.of(authorizedUserId))
            .build();

    @Test
    public void uploadFileTest() throws Exception {
        String filename = "test.jpg";
        String folder = "Tests/FileServiceTests";
        String purpose = FilePurposes.TEST;
        byte[] file = TestUtils.generateMockImage();

        when(fileStorage.getProvider()).thenReturn(StorageProviders.IMAGEKIT);
        when(fileStorage.uploadFile(file, filename, folder, false)).thenReturn(uploadedFile);
        when(fileRepository.save(any(FileEntity.class))).thenReturn(uploadedFileEntity);

        fileService.uploadFile(file, filename, folder, purpose, authorizedUserId, null);
        FileEntity fileEntity = fileService.uploadFile(file, filename, folder, purpose, authorizedUserId, null);
        assertNotNull(fileEntity);
        assertNotNull(fileEntity.getId());
        assertNotNull(fileEntity.getProvider());
        assertNotNull(fileEntity.getData());
    }

    @Test
    public void getPublicFileUrlFromValidFileIdTest() throws Exception {
        when(fileRepository.findById(fileId)).thenReturn(java.util.Optional.of(uploadedFileEntity));
        when(fileStorage.getFileUrl(uploadedFileEntity.getData(), null, null)).thenReturn(uploadedFile.getUrl());
        String fileUrl = fileService.getFileUrl(fileId);
        assertNotNull(fileUrl);
    }

    @Test
    public void getResizedPublicFileUrlFromValidFileIdTest() throws Exception {
        when(fileRepository.findById(fileId)).thenReturn(java.util.Optional.of(uploadedFileEntity));
        when(fileStorage.getFileUrl(uploadedFileEntity.getData(), resize, resize)).thenReturn(uploadedFile.getUrl());
        String fileUrl = fileService.getFileUrl(fileId, resize, resize);
        assertNotNull(fileUrl);
    }

    @Test
    public void getFileUrlFromInvalidFileIdTest() {
        assertThrowsExactly(FileNotFoundException.class, () -> fileService.getFileUrl(invalidFileId));
    }

    @Test
    public void getFileSignedUrlFromValidFileIdTest() throws Exception {
        when(fileRepository.findById(fileId)).thenReturn(java.util.Optional.of(uploadedPrivateFileEntity));
        when(fileStorage.generateSignedUrl(uploadedPrivateFileEntity.getData(), expiredInSeconds, null, null)).thenReturn(uploadedPrivateFile.getUrl());
        String fileUrl = fileService.getFileSignedUrl(fileId, authorizedUserId, expiredInSeconds);
        assertNotNull(fileUrl);
    }

    @Test
    public void getResizedFileSignedUrlFromValidFileIdTest() throws Exception {
        when(fileRepository.findById(fileId)).thenReturn(java.util.Optional.of(uploadedPrivateFileEntity));
        when(fileStorage.generateSignedUrl(uploadedPrivateFileEntity.getData(), expiredInSeconds, resize, resize)).thenReturn(uploadedPrivateFile.getUrl());
        String fileUrl = fileService.getFileSignedUrl(fileId, authorizedUserId, expiredInSeconds, resize, resize);
        assertNotNull(fileUrl);
    }

    @Test
    public void getFileSignedUrlFromValidFileIdWithUnauthorizedUserTest() {
        when(fileRepository.findById(fileId)).thenReturn(java.util.Optional.of(uploadedPrivateFileEntity));
        assertThrowsExactly(FileAccessDeniedException.class, () -> fileService.getFileSignedUrl(fileId, unauthorizedUserId, expiredInSeconds));
    }

    @Test
    public void deleteFileTest() throws Exception {
        when(fileRepository.findById(fileId)).thenReturn(java.util.Optional.of(uploadedPrivateFileEntity));
        when(fileStorage.deleteFile(uploadedPrivateFileEntity.getData())).thenReturn(true);
        boolean isDeleted = fileService.deleteFile(fileId, authorizedUserId);
        assertTrue(isDeleted);
    }

    @Test
    public void deleteFileWithUnauthorizedUserTest() {
        when(fileRepository.findById(fileId)).thenReturn(java.util.Optional.of(uploadedPrivateFileEntity));
        assertThrowsExactly(FileAccessDeniedException.class, () -> fileService.deleteFile(fileId, unauthorizedUserId));
    }
}
