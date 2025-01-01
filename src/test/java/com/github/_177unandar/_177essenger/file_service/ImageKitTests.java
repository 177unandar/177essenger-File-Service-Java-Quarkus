package com.github._177unandar._177essenger.file_service;

import com.github._177unandar._177essenger.file_service.adapter.out.storages.ImageKitFileStorage;
import com.github._177unandar._177essenger.file_service.core.domain.exceptions.UploadFileException;
import com.google.gson.Gson;
import io.imagekit.sdk.exceptions.*;
import io.imagekit.sdk.models.results.Result;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static com.github._177unandar._177essenger.file_service.TestUtils.getUrlResponseCode;
import static org.junit.jupiter.api.Assertions.*;

// Assuming ImageKitFileStorage, MockMultipartFile, ImageKitResponse, TestUtils are defined elsewhere (or you can replace their functionalities)
public class ImageKitTests {

    private final ImageKitFileStorage fileStorage;
    private final byte[] mockImage;

    public ImageKitTests() throws Exception {
        fileStorage = new ImageKitFileStorage(new Gson());
        mockImage = TestUtils.generateMockImage();
    }

    /**
     * Uploads a test image to the ImageKit storages and verifies the upload.
     *
     * @param isPrivate Indicates whether the image should be uploaded as private.
     * @return The result from ImageKit containing the uploaded file's details.
     * @throws UploadFileException If there is an error during the upload process.
     */
    private Result testUploadImage(boolean isPrivate) throws UploadFileException {

        String folder = "Tests";
        Result imageKit = (Result) fileStorage.uploadFile(mockImage, "test.png", folder, isPrivate);

        assertNotNull(imageKit);
        assertNotNull(imageKit.getFileId());
        assertNotNull(imageKit.getName());
        assertNotNull(imageKit.getFilePath());
        assertNotNull(imageKit.getUrl());
        return imageKit;
    }

    /**
     * Verifies that uploading a public image to ImageKit works by uploading an image, retrieving its URL, and verifying that the URL is accessible.
     * <p>
     * This test also verifies that resizing the image works by requesting a resized version of the image, and verifying that the resized URL is accessible.
     * <p>
     * Finally, the test verifies that deleting the image works by deleting the image and verifying that the URL is no longer accessible.
     *
     * @throws UploadFileException      If there is an error during the upload process.
     * @throws ForbiddenException       If the upload request is forbidden.
     * @throws TooManyRequestsException If the upload request exceeds the rate limit.
     * @throws InternalServerException  If there is an error on the ImageKit server.
     * @throws UnauthorizedException    If the upload request is unauthorized.
     * @throws BadRequestException      If the upload request is invalid.
     * @throws UnknownException         If the upload request is unknown.
     * @throws InterruptedException     If there is an interruption while waiting for the deletion to propagate.
     */
    @Test
    public void testProcessUploadedPublicImage() throws InterruptedException, UploadFileException, ForbiddenException, TooManyRequestsException, InternalServerException, UnauthorizedException, BadRequestException, UnknownException {
        // Test upload public image
        Result response = testUploadImage(false);

        String jsonResponse = new Gson().toJson(response);
        // Get original image URL
        String originalUrl = fileStorage.getFileUrl(jsonResponse);
        assertNotNull(originalUrl);

        // Check response code of original image URL
        assertEquals(200, getUrlResponseCode(originalUrl));

        // Get resized image URL
        String resizedUrl = fileStorage.getFileUrl(jsonResponse, "100", "100");
        assertNotNull(resizedUrl);

        // Check response code of resized image URL
        assertEquals(200, getUrlResponseCode(resizedUrl));

        // Delete uploaded file
        assertTrue(fileStorage.deleteFile(jsonResponse));

        // Wait for deletion to propagate
        TimeUnit.SECONDS.sleep(3);

        // Check response code after deletion
        assertNotEquals(200, getUrlResponseCode(originalUrl + "?deleted=true"));
    }

    /**
     * Tests the process of uploading a private image to ImageKit, generating signed URLs,
     * and verifying their accessibility and expiration. The test performs the following steps:
     * <p>
     * 1. Uploads an image as private and retrieves the upload response.
     * 2. Generates a signed URL for the original private image and verifies its accessibility.
     * 3. Waits for the signed URL to expire and verifies that it is no longer accessible.
     * 4. Generates a signed URL for a resized version of the private image and verifies its accessibility.
     * 5. Waits for the resized signed URL to expire and verifies that it is no longer accessible.
     * 6. Deletes the uploaded image and ensures proper resource cleanup.
     *
     * @throws UploadFileException      If there is an error during the upload process.
     * @throws ForbiddenException       If the request is forbidden.
     * @throws TooManyRequestsException If the request exceeds the rate limit.
     * @throws InternalServerException  If there is an error on the ImageKit server.
     * @throws UnauthorizedException    If the request is unauthorized.
     * @throws BadRequestException      If the request is invalid.
     * @throws UnknownException         If the request is unknown.
     * @throws InterruptedException     If there is an interruption while waiting for expiration.
     */
    @Test
    public void testProcessUploadedPrivateImage() throws InterruptedException, UploadFileException, ForbiddenException, TooManyRequestsException, InternalServerException, UnauthorizedException, BadRequestException, UnknownException {
        // Test upload private image
        Result response = testUploadImage(true);
        String jsonResponse = new Gson().toJson(response);

        int expiredInSeconds = 5;
        int waitingTimeForTest = 1;

        // Get original signed image URL
        String originalUrl = fileStorage.generateSignedUrl(jsonResponse, expiredInSeconds);
        assertNotNull(originalUrl);

        // Check response code of original signed image URL
        assertEquals(200, getUrlResponseCode(originalUrl));

        // Wait for expiration
        TimeUnit.MILLISECONDS.sleep((expiredInSeconds + waitingTimeForTest) * 1000);

        // Check response code of expired original signed image URL
        assertNotEquals(200, getUrlResponseCode(originalUrl));

        // Get resized signed image URL
        String resizedUrl = fileStorage.generateSignedUrl(jsonResponse, expiredInSeconds, "100", "100");
        assertNotNull(resizedUrl);

        // Check response code of resized signed image URL
        assertEquals(200, getUrlResponseCode(resizedUrl));

        // Wait for expiration
        TimeUnit.MILLISECONDS.sleep((expiredInSeconds + waitingTimeForTest) * 1000);

        // Check response code of expired resized signed image URL
        assertNotEquals(200, getUrlResponseCode(resizedUrl));

        // Delete uploaded file
        assertTrue(fileStorage.deleteFile(jsonResponse));
    }


}