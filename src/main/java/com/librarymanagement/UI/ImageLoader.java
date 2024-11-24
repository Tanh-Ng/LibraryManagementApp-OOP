package com.librarymanagement.UI;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ConcurrentHashMap;

import com.librarymanagement.model.Book;
import javafx.scene.image.Image;

public class ImageLoader {
    private static final ExecutorService executorService = Executors.newFixedThreadPool(5);
    private static final ConcurrentHashMap<String, Image> imageCache = new ConcurrentHashMap<>();

    // Preload an image asynchronously
    public static void preloadImage(String Url) {
        String imageUrl = Url;
        if (imageUrl == null || imageCache.containsKey(imageUrl)) {
            return; // Skip if already loaded or URL is invalid
        }

        executorService.submit(() -> {
            try {
                if (imageUrl != null) {
                    Image image = new Image(imageUrl, true); // True for asynchronous loading
                    imageCache.put(imageUrl, image);
                }
            } catch (Exception e) {
                System.out.println("Error preloading image for ISBN " + e.getMessage());
            }
        });
    }

    // Get a preloaded image or placeholder
    public static Image getImage(String url) {
        return imageCache.get(url);
    }

    // Shutdown the executor service
    public static void shutdown() {
        executorService.shutdown();
    }
}
