package com.github._177unandar._177essenger.file_service.adapter.out.storages;

import com.github._177unandar._177essenger.file_service.core.contracts.ErrorCodes;
import com.github._177unandar._177essenger.file_service.core.contracts.StorageProviders;
import com.github._177unandar._177essenger.file_service.core.domain.exceptions.UploadFileException;
import com.github._177unandar._177essenger.file_service.core.storages.FileStorage;
import com.google.gson.Gson;
import io.imagekit.sdk.ImageKit;
import io.imagekit.sdk.config.Configuration;
import io.imagekit.sdk.exceptions.*;
import io.imagekit.sdk.models.FileCreateRequest;
import io.imagekit.sdk.models.results.Result;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class ImageKitFileStorage implements FileStorage {

    private final ImageKit imageKit;
    private final Gson gson;

    public ImageKitFileStorage(Gson gson) {
        imageKit = ImageKit.getInstance();
        String publicKey = System.getenv("IMAGEKIT_PUBLIC_KEY");
        String privateKey = System.getenv("IMAGEKIT_PRIVATE_KEY");
        String urlEndpoint = System.getenv("IMAGEKIT_URL_ENDPOINT");
        System.out.println("publicKey : " + publicKey);
        System.out.println("privateKey : " + privateKey);
        System.out.println("urlEndpoint : " + urlEndpoint);
        Configuration config = new Configuration(publicKey, privateKey, urlEndpoint);
        imageKit.setConfig(config);
        this.gson = gson;
    }

    @Override
    public String getProvider() {
        return StorageProviders.IMAGEKIT;
    }

    @Override
    public Object uploadFile(byte[] file, String filename, String folder, boolean isPrivate) throws UploadFileException {
        FileCreateRequest fileCreateRequest = new FileCreateRequest(file, filename);
        List<String> responseFields = new ArrayList<>();
        responseFields.add("thumbnail");
        fileCreateRequest.setFolder(folder);
        fileCreateRequest.setPrivateFile(isPrivate);
        fileCreateRequest.setResponseFields(responseFields);
        try {
            return imageKit.upload(fileCreateRequest);
        } catch (InternalServerException | UnknownException e) {
            throw new UploadFileException(ErrorCodes.UPLOAD_ERROR, this.getClass().getSimpleName(), e.getMessage());
        } catch (BadRequestException e) {
            throw new UploadFileException(ErrorCodes.UPLOAD_BAD_REQUEST, this.getClass().getSimpleName(), e.getMessage());
        } catch (ForbiddenException e) {
            throw new UploadFileException(ErrorCodes.UPLOAD_FORBIDDEN, this.getClass().getSimpleName(), e.getMessage());
        } catch (TooManyRequestsException e) {
            throw new UploadFileException(ErrorCodes.UPLOAD_REACH_LIMIT, this.getClass().getSimpleName(), e.getMessage());
        } catch (UnauthorizedException e) {
            throw new UploadFileException(ErrorCodes.UPLOAD_UNAUTHORIZED, this.getClass().getSimpleName(), e.getMessage());
        }
    }

    @Override
    public String generateSignedUrl(String fileData, int expireInSeconds, String width, String height) {
        Result result = gson.fromJson(fileData, Result.class);
        Map<String, Object> options = new HashMap<>();
        options.put("path", result.getFilePath());
        options.put("signed", true);
        options.put("expireSeconds", expireInSeconds);

        return getImageKitUrl(width, height, options);
    }

    @Override
    public String getFileUrl(String fileData) {
        return getFileUrl(fileData, null, null);
    }

    @Override
    public String getFileUrl(String fileData, String width, String height) {
        Result result = gson.fromJson(fileData, Result.class);
        Map<String, Object> options = new HashMap<>();
        options.put("path", result.getFilePath());
        return getImageKitUrl(width, height, options);
    }

    @Override
    public String generateSignedUrl(String fileData, int expireInSeconds) {
        return generateSignedUrl(fileData, expireInSeconds, null, null);
    }

    private String getImageKitUrl(String width, String height, Map<String, Object> options) {
        List<Map<String, String>> transformation = new ArrayList<>();
        if (width != null && height != null) {
            Map<String, String> scale = new HashMap<>();
            scale.put("height", height);
            scale.put("width", width);
            transformation.add(scale);
        }
        options.put("transformation", transformation);
        return imageKit.getUrl(options);
    }

    @Override
    public boolean deleteFile(String fileData) throws ForbiddenException, TooManyRequestsException, InternalServerException, UnauthorizedException, BadRequestException, UnknownException {
        Result result = gson.fromJson(fileData, Result.class);
        Result response = imageKit.deleteFile(result.getFileId());
        return response != null;
    }
}