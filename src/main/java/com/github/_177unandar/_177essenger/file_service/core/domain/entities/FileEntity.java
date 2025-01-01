package com.github._177unandar._177essenger.file_service.core.domain.entities;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Builder
@Getter
public class FileEntity {
    private String id;
    private String provider;
    private String purpose;
    private String data; // JSON data as a string
    private UUID uploadedBy;
    private List<UUID> permission;
}
