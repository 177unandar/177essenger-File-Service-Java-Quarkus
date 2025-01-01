package com.github._177unandar._177essenger.file_service.core.storages;

import com.github._177unandar._177essenger.file_service.core.domain.exceptions.UploadFileException;
import io.imagekit.sdk.exceptions.*;

public interface FileStorage {

    /**
     * Gets the name of the provider that these storages uses.
     *
     * @return A string that represents the name of the provider.
     */
    String getProvider();

    /**
     * Uploads a file to the storages provider and returns the uploaded file data.
     *
     * @param file      The file to be uploaded.
     * @param filename  The name of the file to be uploaded.
     * @param folder    The folder to save the file to.
     * @param isPrivate Whether the file should be saved as private or not.
     * @return The uploaded file data.
     */
    Object uploadFile(byte[] file, String filename, String folder, boolean isPrivate) throws UploadFileException;


    /**
     * Gets the URL of a file given the file data.
     *
     * @param fileData The Json String file data, typically obtained from the uploadFile response.
     * @return The URL of the file.
     */
    String getFileUrl(String fileData);

    /**
     * Gets the URL of a file given the file data with width and height parameters.
     *
     * @param fileData The Json String file data, typically obtained from the uploadFile response.
     * @param width    The desired width of the file. If null, the original width is used.
     * @param height   The desired height of the file. If null, the original height is used.
     * @return The URL of the file with the specified dimensions.
     */
    String getFileUrl(String fileData, String width, String height);

    /**
     * Generates a signed URL for accessing a file.
     *
     * @param fileData        The Json String file data, typically obtained from the uploadFile response.
     * @param expireInSeconds The time in seconds after which the signed URL will expire.
     * @return A signed URL that provides access to the file until the expiration time.
     */
    String generateSignedUrl(String fileData, int expireInSeconds);

    /**
     * Generates a signed URL for accessing a file, with width and height parameters.
     *
     * @param fileData        The Json String file data, typically obtained from the uploadFile response.
     * @param expireInSeconds The time in seconds after which the signed URL will expire.
     * @param width           The desired width of the file. If null, the original width is used.
     * @param height          The desired height of the file. If null, the original height is used.
     * @return A signed URL that provides access to the file with the specified dimensions until the expiration time.
     */
    String generateSignedUrl(String fileData, int expireInSeconds, String width, String height);

    /**
     * Deletes a file from the storages' provider.
     *
     * @param fileData The Json String file data, typically obtained from the uploadFile response.
     * @return A boolean indicating whether the file was successfully deleted.
     */
    boolean deleteFile(String fileData) throws ForbiddenException, TooManyRequestsException, InternalServerException, UnauthorizedException, BadRequestException, UnknownException;

}
