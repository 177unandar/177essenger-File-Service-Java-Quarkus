package com.github._177unandar.file_service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TestUtils {

    /**
     * Generates a byte array representing a PNG image, which is a solid blue
     * square with dimensions of 300x300.
     *
     * @return A byte array containing the PNG image data.
     * @throws Exception If there is an error while generating the image.
     */
    public static byte[] generateMockImage() throws Exception {
        // Create a blank image with a white background
        int width = 300;
        int height = 300;
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = img.createGraphics();
        graphics.setColor(Color.BLUE);
        graphics.fillRect(0, 0, width, height);
        graphics.dispose();

        // Encode the image into a byte array as PNG
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(img, "png", outputStream);
        return outputStream.toByteArray();
    }

    /**
     * Sends an HTTP GET request to the specified URL and returns the response status code.
     *
     * @param url The URL to which the GET request is sent.
     * @return The HTTP response status code.
     * @throws RuntimeException If an I/O error occurs when sending or receiving, or if the operation is interrupted.
     */
    public static int getUrlResponseCode(String url) {
        // Create an HttpClient instance
        try (HttpClient httpClient = HttpClient.newHttpClient()) {

            // Build the HttpRequest
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url)) // Replace URL with URI
                    .GET()
                    .build();

            // Send the request and get the response
            try {
                HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
                // Return the response status code
                return response.statusCode();
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }


}
