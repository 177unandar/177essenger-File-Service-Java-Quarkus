package com.github._177unandar.file_service.core.domain.exceptions;


import com.github._177unandar.file_service.core.contracts.ErrorCodes;

public class FileAccessDeniedException extends Exception {

    private final ErrorCodes errorCode;
    private final String entity;

    public FileAccessDeniedException(String entity) {
        this(ErrorCodes.ACCESS_DENIED, entity, "Access denied");
    }

    public FileAccessDeniedException(ErrorCodes errorCode, String entity, String message) {
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