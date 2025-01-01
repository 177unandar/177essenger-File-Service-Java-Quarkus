package com.github._177unandar._177essenger.file_service.core.domain.usecases;

import com.github._177unandar._177essenger.file_service.core.domain.exceptions.UploadFileException;

import java.util.UUID;

public interface UploadImageProfileUseCase {

    /**
     * Executes the upload of an image profile file.
     *
     * @param file     The byte array representing the image file to be uploaded.
     * @param filename The name of the file to be uploaded.
     * @param userId   The UUID of the user who is uploading the file.
     * @return The ID of the uploaded file.
     * @throws UploadFileException If an error occurs during the upload process.
     */
    String execute(byte[] file, String filename, UUID userId) throws UploadFileException;
}
