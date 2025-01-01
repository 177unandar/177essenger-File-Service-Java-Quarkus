package com.github._177unandar._177essenger.file_service.core.domain.exceptions;


import com.github._177unandar._177essenger.file_service.core.contracts.ErrorCodes;
import lombok.Getter;

@Getter
public class FileNotFoundException extends Exception {

    private final ErrorCodes errorCode;
    private final String entity;

    public FileNotFoundException(String entity) {
        this(ErrorCodes.FILE_NOT_FOUND, entity, "File not found");
    }

    public FileNotFoundException(ErrorCodes errorCode, String entity, String message) {
        super(message);
        this.errorCode = errorCode;
        this.entity = entity;
    }

}