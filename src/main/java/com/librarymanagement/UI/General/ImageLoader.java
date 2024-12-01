package com.librarymanagement.UI.General;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ConcurrentHashMap;

import com.librarymanagement.model.Book;
import javafx.scene.image.Image;

public class ImageLoader {
    private static final ExecutorService executorService = Executors.newFixedThreadPool(5);
    private static final ConcurrentHashMap<String, Image> imageCache = new ConcurrentHashMap<>(); // Cache for book covers
    private static final ConcurrentHashMap<String, Image> qrCodeCache = new ConcurrentHashMap<>(); // Cache for QR codes

    private static final String COVER_CACHE_DIR = "cache/cover/"; // Directory for book cover images
    private static final String QR_CACHE_DIR = "cache/qr/"; // Directory for QR code images

    // Preload an image asynchronously and store it in the cache folder
    public static void preloadImage(Book book) {
        String imageUrl = book.getImageUrl();
        if (imageUrl == null) {
            return; // Skip if URL is invalid
        }

        // Check if image is already in cache (either in memory or on disk)
        if (imageCache.containsKey(imageUrl)) {
            return; // Skip if already loaded
        }

        // Check if the image already exists in the local cache
        File cachedFile = new File(COVER_CACHE_DIR + getFileNameFromUrl(imageUrl));
        if (cachedFile.exists()) {
            // If the image is cached on disk, load it from there
            Image image = new Image(cachedFile.toURI().toString());
            imageCache.put(imageUrl, image);
        } else {
            // Otherwise, download the image and store it in the cache
            executorService.submit(() -> {
                try {
                    // Download the image
                    URL url = new URL(imageUrl);
                    try (InputStream inputStream = url.openStream()) {
                        Files.createDirectories(Paths.get(COVER_CACHE_DIR)); // Ensure the cache folder exists
                        File outputFile = new File(COVER_CACHE_DIR + getFileNameFromUrl(imageUrl));

                        try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                            byte[] buffer = new byte[4096];
                            int bytesRead;
                            while ((bytesRead = inputStream.read(buffer)) != -1) {
                                outputStream.write(buffer, 0, bytesRead);
                            }
                        }

                        // Now, load the image into the cache
                        Image image = new Image(outputFile.toURI().toString());
                        imageCache.put(imageUrl, image);
                    }
                } catch (IOException e) {
                    System.out.println("Error downloading or saving image for ISBN " + book.getIsbn() + ": " + e.getMessage());
                }
            });
        }
    }

    // Preload a QR code image asynchronously and store it in the cache folder
    public static void preloadQRCode(String qrCodeUrl) {
        if (qrCodeUrl == null) {
            return; // Skip if URL is invalid
        }

        // Check if QR code is already in cache (either in memory or on disk)
        if (qrCodeCache.containsKey(qrCodeUrl)) {
            return; // Skip if already loaded
        }

        // Check if the QR code already exists in the local cache
        File cachedFile = new File(QR_CACHE_DIR + getQRCodeFileName(qrCodeUrl));
        if (cachedFile.exists()) {
            // If the QR code is cached on disk, load it from there
            Image qrCodeImage = new Image(cachedFile.toURI().toString());
            qrCodeCache.put(qrCodeUrl, qrCodeImage);
        } else {
            // Otherwise, download the QR code and store it in the cache
            executorService.submit(() -> {
                try {
                    // Download the QR code image
                    URL url = new URL(qrCodeUrl);
                    try (InputStream inputStream = url.openStream()) {
                        Files.createDirectories(Paths.get(QR_CACHE_DIR)); // Ensure the cache folder exists
                        File outputFile = new File(QR_CACHE_DIR + getQRCodeFileName(qrCodeUrl));

                        try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                            byte[] buffer = new byte[4096];
                            int bytesRead;
                            while ((bytesRead = inputStream.read(buffer)) != -1) {
                                outputStream.write(buffer, 0, bytesRead);
                            }
                        }

                        // Now, load the QR code image into the cache
                        Image qrCodeImage = new Image(outputFile.toURI().toString());
                        qrCodeCache.put(qrCodeUrl, qrCodeImage);
                    }
                } catch (IOException e) {
                    System.out.println("Error downloading or saving QR code image: " + e.getMessage());
                }
            });
        }
    }

    // Get a preloaded image or placeholder for book covers
    public static Image getImage(String url) {
        if (url == null) {
            System.out.println("Warning: Attempted to fetch an image with a null URL.");
            return null;
        }
        return imageCache.get(url);
    }

    // Get a preloaded QR code or placeholder
    public static Image getQRCode(String qrCodeUrl) {
        if (qrCodeUrl == null) {
            System.out.println("Warning: Attempted to fetch a QR code with a null URL.");
            return null;
        }
        System.out.println(qrCodeUrl);
        return qrCodeCache.get(qrCodeUrl);
    }

    // Helper function to generate a valid file name from a URL for book covers
    private static String getFileNameFromUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1); // Extract the file name from the URL
    }

    // Helper function to generate a valid file name for QR codes
    private static String getQRCodeFileName(String qrCodeUrl) {
        return "qr_" + qrCodeUrl.substring(qrCodeUrl.lastIndexOf("/") + 1); // Prefix for QR code files
    }

    // Get the writable cache directory for book covers
    private static String getCoverCacheDir() {
        return "cache/cover/"; // Using relative directory for book cover images
    }

    // Get the writable cache directory for QR codes
    private static String getQRCodeCacheDir() {
        return "cache/qr/"; // Using relative directory for QR code images
    }

    // Shutdown the executor service
    public static void shutdown() {
        executorService.shutdown();
    }

    // Optionally clean up cache folder (e.g., after a certain time or when app closes)
    public static void cleanCache() {
        try {
            // Clean book cover cache
            Files.walk(Paths.get(COVER_CACHE_DIR))
                    .filter(Files::isRegularFile)
                    .forEach(file -> {
                        try {
                            Files.delete(file);
                        } catch (IOException e) {
                            System.out.println("Error cleaning up cover cache: " + e.getMessage());
                        }
                    });

            // Clean QR code cache
            Files.walk(Paths.get(QR_CACHE_DIR))
                    .filter(Files::isRegularFile)
                    .forEach(file -> {
                        try {
                            Files.delete(file);
                        } catch (IOException e) {
                            System.out.println("Error cleaning up QR code cache: " + e.getMessage());
                        }
                    });

        } catch (IOException e) {
            System.out.println("Error walking cache directory: " + e.getMessage());
        }
    }
}
