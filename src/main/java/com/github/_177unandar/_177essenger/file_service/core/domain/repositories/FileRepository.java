package com.github._177unandar._177essenger.file_service.core.domain.repositories;

import com.github._177unandar._177essenger.file_service.core.domain.entities.FileEntity;

import java.util.Optional;

public interface FileRepository {
    /**
     * Saves a file entity to the database.
     *
     * @param file The file entity to be saved.
     * @return The saved file entity.
     */
    FileEntity save(FileEntity file);

    /**
     * Retrieves a file entity from the database by id.
     *
     * @param id The id of the file entity to be retrieved.
     * @return The file entity if found, otherwise an empty Optional.
     */
    Optional<FileEntity> findById(String id);

    /**
     * Deletes a file entity from the database by id.
     *
     * @param id The id of the file entity to be deleted.
     */
    void deleteById(String id);
}
