package com.github._177unandar._177essenger.file_service.adapter.out.repositories;

import com.github._177unandar._177essenger.file_service.core.domain.entities.FileEntity;
import com.github._177unandar._177essenger.file_service.core.domain.repositories.FileRepository;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class MongoFileRepository implements FileRepository, PanacheMongoRepository<FileEntity> {

    @Override
    public FileEntity save(FileEntity file) {
        persist(file);
        return file;
    }

    @Override
    public Optional<FileEntity> findById(String id) {
        return find("id", id).firstResultOptional();
    }

    @Override
    public void deleteById(String id) {
        delete("id", id);
    }
}
