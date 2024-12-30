package com.github._177unandar.file_service.core.contracts;

public enum ErrorCodes {
    UPLOAD_ERROR("UPLOAD_ERROR", "5001"),
    UPLOAD_BAD_REQUEST("UPLOAD_BAD_REQUEST", "5002"),
    UPLOAD_FORBIDDEN("UPLOAD_FORBIDDEN", "5003"),
    UPLOAD_REACH_LIMIT("UPLOAD_REACH_LIMIT", "5004"),
    UPLOAD_UNAUTHORIZED("UPLOAD_UNAUTHORIZED", "5005"),
    FILE_NOT_FOUND("FILE_NOT_FOUND", "5006"),
    ACCESS_DENIED("ACCESS_DENIED", "5007");

    private final String message;
    private final String code;

    ErrorCodes(String message, String code) {
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }
}
