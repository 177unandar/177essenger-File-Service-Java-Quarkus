package com.github._177unandar._177essenger.file_service;

import com.github._177unandar._177essenger.file_service.adapter.out.repositories.MongoFileRepository;
import com.github._177unandar._177essenger.file_service.adapter.out.storages.ImageKitFileStorage;
import com.github._177unandar._177essenger.file_service.core.contracts.FileFolders;
import com.github._177unandar._177essenger.file_service.core.contracts.StorageProviders;
import com.github._177unandar._177essenger.file_service.core.domain.entities.FileEntity;
import com.github._177unandar._177essenger.file_service.core.domain.repositories.FileRepository;
import com.github._177unandar._177essenger.file_service.core.domain.usecases.UploadImageProfileUseCase;
import com.github._177unandar._177essenger.file_service.core.domain.usecases.UploadImageProfileUseCaseImpl;
import com.github._177unandar._177essenger.file_service.core.services.FileService;
import com.github._177unandar._177essenger.file_service.core.services.FileServiceImpl;
import com.github._177unandar._177essenger.file_service.core.storages.FileStorage;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UseCasesTests {

    private final Gson gson = new Gson();
    private final FileStorage fileStorage;
    private final FileRepository fileRepository;
    private final FileService fileService;
    private final UploadImageProfileUseCase uploadImageProfileUseCase;

    public UseCasesTests() {
        this.fileStorage = mock(ImageKitFileStorage.class);
        this.fileRepository = mock(MongoFileRepository.class);
        this.fileService = new FileServiceImpl(fileRepository, fileStorage, gson);
        this.uploadImageProfileUseCase = new UploadImageProfileUseCaseImpl(fileService);
    }

    private final String storageEndPoint = System.getenv("IMAGEKIT_URL_ENDPOINT");
    private final String sampleImage = System.getenv("IMAGEKIT_SAMPLE_IMAGE");
    private final String samplePrivateImage = System.getenv("IMAGEKIT_SAMPLE_PRIVATE_IMAGE");

    private final String fileId = "1";
    private final String invalidFileId = "404";
    private final UUID authorizedUserId = UUID.randomUUID();
    private final UUID unauthorizedUserId = UUID.randomUUID();
    private final String resize = "100";
    private final int expiredInSeconds = 60;

    private final FileStorageTestEntity uploadedFile = new FileStorageTestEntity(storageEndPoint, sampleImage);
    private final FileStorageTestEntity uploadedPrivateFile = new FileStorageTestEntity(storageEndPoint, samplePrivateImage);

    @Test
    public void uploadImageProfileUseCaseTest() throws Exception {
        byte[] file = TestUtils.generateMockImage();
        String filename = "test.jpg";
        FileEntity fileEntity = FileEntity.builder()
                .id(fileId)
                .provider(StorageProviders.IMAGEKIT)
                .purpose(FileFolders.IMAGE_PROFILE)
                .data(gson.toJson(uploadedFile))
                .uploadedBy(authorizedUserId)
                .build();

        when(fileStorage.uploadFile(file, filename, FileFolders.IMAGE_PROFILE, false)).thenReturn(uploadedFile);
        when(fileRepository.save(any(FileEntity.class))).thenReturn(fileEntity);
        String fileId = uploadImageProfileUseCase.execute(file, filename, authorizedUserId);
        assertEquals(fileId, fileEntity.getId());
    }
}
