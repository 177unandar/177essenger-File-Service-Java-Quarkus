package com.github._177unandar._177essenger.file_service.core.contracts;

import com.github._177unandar._177essenger.file_service.core.domain.entities.FileEntity;
import com.github._177unandar._177essenger.file_service.core.domain.exceptions.FileAccessDeniedException;
import com.github._177unandar._177essenger.file_service.core.domain.exceptions.FileNotFoundException;
import com.github._177unandar._177essenger.file_service.core.domain.exceptions.UploadFileException;
import io.imagekit.sdk.exceptions.*;

import java.util.List;
import java.util.UUID;

public interface FileService {

    /**
     * Uploads a file to the storages provider and returns the uploaded file data.
     * If the file is private, the given permissions will be used to determine
     * who can access the file.
     *
     * @param file        The file to be uploaded.
     * @param filename    The name of the file to be uploaded.
     * @param folder      The folder to save the file to.
     * @param purpose     The purpose of the file.
     * @param uploadedBy  The user who uploaded the file.
     * @param permissions The user IDs that are allowed to access the file.
     * @return The uploaded file data.
     * @throws UploadFileException If any error occurs during the uploading process.
     */
    public FileEntity uploadFile(byte[] file, String filename, String folder, String purpose, UUID uploadedBy, List<UUID> permissions) throws UploadFileException;

    /**
     * Gets the URL of a file given the file ID.
     *
     * @param fileId The ID of the file.
     * @return The URL of the file.
     * @throws UploadFileException   If any error occurs during the uploading process.
     * @throws FileNotFoundException If the file was not found.
     */
    public String getFileUrl(String fileId) throws UploadFileException, FileNotFoundException;

    /**
     * Gets the URL of a file given the file ID and optional width and height parameters.
     *
     * @param fileId The ID of the file.
     * @param width  The width of the file.
     * @param height The height of the file.
     * @return The URL of the file with the specified dimensions.
     * @throws UploadFileException   If any error occurs during the uploading process.
     * @throws FileNotFoundException If the file was not found.
     */
    public String getFileUrl(String fileId, String width, String height) throws UploadFileException, FileNotFoundException;

    /**
     * Gets a signed URL for accessing a file given the file ID and optional width and height parameters.
     * The URL will expire after the specified number of seconds.
     *
     * @param fileId          The ID of the file.
     * @param userId          The user ID of the user who is accessing the file.
     * @param expireInSeconds The number of seconds after which the signed URL will expire.
     * @return The signed URL of the file with the specified dimensions.
     * @throws UploadFileException       If any error occurs during the uploading process.
     * @throws FileNotFoundException     If the file was not found.
     * @throws FileAccessDeniedException If the user does not have access to the file.
     */
    public String getFileSignedUrl(String fileId, UUID userId, int expireInSeconds) throws UploadFileException, FileNotFoundException, FileAccessDeniedException;

    /**
     * Gets a signed URL for accessing a file given the file ID and optional width and height parameters.
     * The URL will expire after the specified number of seconds.
     *
     * @param fileId          The ID of the file.
     * @param userId          The user ID of the user who is accessing the file.
     * @param expireInSeconds The number of seconds after which the signed URL will expire.
     * @param width           The width of the file.
     * @param height          The height of the file.
     * @return The signed URL of the file with the specified dimensions.
     * @throws UploadFileException       If any error occurs during the uploading process.
     * @throws FileNotFoundException     If the file was not found.
     * @throws FileAccessDeniedException If the user does not have access to the file.
     */
    public String getFileSignedUrl(String fileId, UUID userId, int expireInSeconds, String width, String height) throws UploadFileException, FileNotFoundException, FileAccessDeniedException;

    /**
     * Deletes a file given its ID and the ID of the user who is deleting the file.
     *
     * @param fileId The ID of the file to be deleted.
     * @param userId The ID of the user who is deleting the file.
     * @return true if the file was successfully deleted, false otherwise.
     * @throws UploadFileException       If any error occurs during the uploading process.
     * @throws ForbiddenException        If the user does not have the necessary permissions to delete the file.
     * @throws TooManyRequestsException  If the user is sending too many requests.
     * @throws InternalServerException   If an internal error occurs in the server.
     * @throws UnauthorizedException     If the user is not authorized to delete the file.
     * @throws BadRequestException       If the request is malformed.
     * @throws UnknownException          If an unknown error occurs.
     * @throws FileNotFoundException     If the file was not found.
     * @throws FileAccessDeniedException If the user does not have access to the file.
     */
    public boolean deleteFile(String fileId, UUID userId) throws UploadFileException, ForbiddenException, TooManyRequestsException, InternalServerException, UnauthorizedException, BadRequestException, UnknownException, FileNotFoundException, FileAccessDeniedException;
}
