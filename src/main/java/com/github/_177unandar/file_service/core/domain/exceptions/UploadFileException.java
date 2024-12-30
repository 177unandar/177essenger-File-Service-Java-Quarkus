package com.github._177unandar.file_service.core.domain.exceptions;

import com.github._177unandar.file_service.core.contracts.ErrorCodes;

public class UploadFileException extends Exception {

    private final ErrorCodes errorCode;
    private final String entity;

    public UploadFileException(ErrorCodes errorCode, String entity, String message) {
        super(message);
        this.errorCode = errorCode;
        this.entity = entity;
    }

    public ErrorCodes getErrorCode() {
        return errorCode;
    }

    public String getEntity() {
        return entity;
    }
}