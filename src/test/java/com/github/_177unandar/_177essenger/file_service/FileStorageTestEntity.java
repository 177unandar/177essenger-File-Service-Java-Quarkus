package com.github._177unandar._177essenger.file_service;

public class FileStorageTestEntity {
    private final String id = "1";
    private final String filePath;
    private final String url;

    public FileStorageTestEntity(String storageEndPoint, String filePath) {
        this.filePath = filePath;
        this.url = storageEndPoint + filePath;
    }

    public String getId() {
        return id;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getUrl() {
        return url;
    }


}
